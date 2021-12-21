package ua.nure.shliakhtin.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.java.Log;
import ua.nure.shliakhtin.constant.AppConstant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public class NavigatorAgent extends Agent {

    @Override
    protected void setup() {
        log.log(Level.SEVERE, "Navigator: Hello! The navigator agent " + getAID().getName() + " is ready.");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AppConstant.NAVIGATOR_AGENT_TYPE);
        sd.setName(AppConstant.NAVIGATOR_SERVICE_DESCRIPTION);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            log.log(Level.WARNING, "Cannot register", fe);
        }

        addBehaviour(new LocationRequestsServer());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            log.log(Level.WARNING, "Cannot deregister", fe);
        }
        log.log(Level.SEVERE, "Navigator: The navigator agent " + getAID().getName() + " terminating.");
    }

    private static class LocationRequestsServer extends CyclicBehaviour {

        final Map<Integer, String> STATES = new LinkedHashMap<Integer, String>() {{
            put(1, "Stench");
            put(2, "Breeze");
            put(3, "Glitter");
            put(4, "Scream");
        }};
        int time = 0;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (parseSpeleologistMessageRequest(msg.getContent())) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REQUEST);
                    reply.setContent(AppConstant.INFORMATION_PROPOSAL_NAVIGATOR);
                    log.log(Level.SEVERE, "Navigator: " + AppConstant.INFORMATION_PROPOSAL_NAVIGATOR);
                    myAgent.send(reply);
                } else if (parseSpeleologistMessageProposal(msg.getContent())) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    String advice = getAdvice(msg.getContent());
                    reply.setContent(advice);
                    log.log(Level.SEVERE, "Navigator: " + advice);
                    myAgent.send(reply);

                } else {
                    log.log(Level.SEVERE, "Navigator: Wrong message!");
                }
            } else {
                block();
            }
        }

        private boolean parseSpeleologistMessageRequest(String instruction) {
            String regex = "\\bAdvice\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }

            return false;
        }

        private boolean parseSpeleologistMessageProposal(String instruction) {
            String regex = "\\bGiving\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }

            return false;
        }

        private String getAdvice(String content) {
            boolean stench = false;
            boolean breeze = false;
            boolean glitter = false;
            boolean scream = false;
            String advicedAction = "";

            for (Map.Entry<Integer, String> entry : STATES.entrySet()) {
                String value = entry.getValue();
                Pattern pattern = Pattern.compile("\\b" + value + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    switch (value) {
                        case "Stench":
                            stench = true;
                        case "Breeze":
                            breeze = true;
                        case "Glitter":
                            glitter = true;
                        case "Scream":
                            scream = true;
                    }
                }
            }

            switch (time) {
                case 0:
                    advicedAction = AppConstant.MESSAGE_FORWARD;
                    time++;
                    break;
                case 1:
                    advicedAction = AppConstant.MESSAGE_RIGHT;
                    time++;
                    break;
                case 2:
                    advicedAction = AppConstant.MESSAGE_FORWARD;
                    time++;
                    break;
                case 3:
                    advicedAction = AppConstant.MESSAGE_LEFT;
                    time++;
                    break;
                case 4:
                    advicedAction = AppConstant.MESSAGE_FORWARD;
                    time++;
                    break;
                case 5:
                    advicedAction = AppConstant.MESSAGE_LEFT;
                    time++;
                    break;
                case 6:
                    advicedAction = AppConstant.MESSAGE_FORWARD;
                    time++;
                    break;
            }
            int rand = 1 + (int) (Math.random() * 3);
            switch (rand) {
                case 1:
                    return AppConstant.ACTION_PROPOSAL1 + advicedAction;
                case 2:
                    return AppConstant.ACTION_PROPOSAL2 + advicedAction;
                case 3:
                    return AppConstant.ACTION_PROPOSAL3 + advicedAction;
                default:
                    return "";
            }
        }
    }
}
