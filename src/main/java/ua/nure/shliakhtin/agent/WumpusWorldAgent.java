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
import ua.nure.shliakhtin.model.AgentPosition;
import ua.nure.shliakhtin.model.Room;
import ua.nure.shliakhtin.model.WumpusCave;
import ua.nure.shliakhtin.model.WumpusPercept;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Log
public class WumpusWorldAgent extends Agent {

    private WumpusCave cave;
    private boolean isWumpusAlive = true;
    private boolean isGoldGrabbed;
    private AgentPosition agentPosition;
    private boolean hasArrow = true;

    public WumpusWorldAgent(WumpusCave cave) {
        this.cave = cave;
    }

    public WumpusWorldAgent() {
        this(new WumpusCave(4, 4, AppConstant.INITIAL_WUMPUS_CAVE));
    }

    public WumpusCave getCave() {
        return cave;
    }

    public boolean isWumpusAlive() {
        return isWumpusAlive;
    }

    public boolean isGoalGrabbed() {
        return isGoldGrabbed;
    }

    public AgentPosition getAgentPosition() {
        return agentPosition;
    }

    @Override
    protected void setup() {
        log.log(Level.SEVERE, "Wumpus world: Hello! The Wumpus world agent " + getAID().getName() + " is ready.");
        log.log(Level.SEVERE, "Wumpus world: Current world state:");
        log.log(Level.SEVERE, cave.toString());

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AppConstant.WUMPUS_WORLD_TYPE);
        sd.setName(AppConstant.WUMPUS_SERVICE_DESCRIPTION);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            log.log(Level.WARNING, "Something went wrong", fe);
        }
        addBehaviour(new SpeleologistConnectPerformer());
        addBehaviour(new WumpusWorldGameInformation());
        addBehaviour(new WumpusWorldGamePerforming());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            log.log(Level.WARNING, "Something went wrong", fe);
        }
        log.log(Level.SEVERE, "Wumpus world: The Wumpus world agent " + getAID().getName() + " terminating.");
    }

    private void turnLeft() {
        agentPosition = cave.turnLeft(agentPosition);
    }

    private void turnRight() {
        agentPosition = cave.turnRight(agentPosition);
    }

    private boolean moveForward() {
        agentPosition = cave.moveForward(agentPosition);
        return (isWumpusAlive && cave.getWumpus().equals(agentPosition.getRoom())) || cave.isPit(agentPosition.getRoom());
    }

    private void grab() {
        if (cave.getGold().equals(agentPosition.getRoom())) {
            isGoldGrabbed = true;
        }
    }

    private void shoot() {
        if (hasArrow && isAgentFacingWumpus(agentPosition))
            isWumpusAlive = false;
    }

    private boolean climb() {
        return agentPosition.getRoom().equals(new Room(1, 1)) && isGoldGrabbed;
    }

    private boolean isAgentFacingWumpus(AgentPosition pos) {
        Room wumpus = cave.getWumpus();
        switch (pos.getOrientation()) {
            case FACING_NORTH:
                return pos.getX() == wumpus.getX() && pos.getY() < wumpus.getY();
            case FACING_SOUTH:
                return pos.getX() == wumpus.getX() && pos.getY() > wumpus.getY();
            case FACING_EAST:
                return pos.getY() == wumpus.getY() && pos.getX() < wumpus.getX();
            case FACING_WEST:
                return pos.getY() == wumpus.getY() && pos.getX() > wumpus.getX();
        }
        return false;
    }

    public WumpusPercept getPerceptSeenBy() {
        WumpusPercept result = new WumpusPercept();
        AgentPosition pos = agentPosition;
        List<Room> adjacentRooms = Arrays.asList(
                new Room(pos.getX() - 1, pos.getY()), new Room(pos.getX() + 1, pos.getY()),
                new Room(pos.getX(), pos.getY() - 1), new Room(pos.getX(), pos.getY() + 1)
        );
        List<Room> adjacentRoomsFull = new LinkedList<>();
        for (Room r : adjacentRooms) {
            adjacentRoomsFull.addAll(Arrays.asList(new Room(r.getX() - 1, r.getY()), new Room(r.getX() + 1, r.getY()),
                    new Room(r.getX(), r.getY() - 1), new Room(r.getX(), r.getY() + 1)));
        }
        adjacentRoomsFull.addAll(adjacentRooms);
        for (Room r : adjacentRoomsFull) {
            if (r.equals(cave.getWumpus()))
                result.setStench();
            if (cave.isPit(r))
                result.setBreeze();
        }
        if (pos.getRoom().equals(cave.getGold()))
            result.setGlitter();
        if (!isWumpusAlive)
            result.setScream();
        return result;
    }

    private void reset() {
        cave = new WumpusCave(4, 4, AppConstant.INITIAL_WUMPUS_CAVE);
        agentPosition = cave.getStart();
        isWumpusAlive = true;
        isGoldGrabbed = false;
        hasArrow = true;
    }

    private class SpeleologistConnectPerformer extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String message = msg.getContent();
                if (Objects.equals(message, AppConstant.GO_INSIDE)) {
                    agentPosition = cave.getStart();
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent(AppConstant.OK_MESSAGE);
                    myAgent.send(reply);
                }

            } else {
                block();
            }
        }
    }

    private class WumpusWorldGameInformation extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String message = msg.getContent();
                if (message.equals(AppConstant.GAME_INFORMATION)) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    WumpusPercept current = getPerceptSeenBy();
                    String g = current.toString();
                    reply.setContent(g);
                    myAgent.send(reply);
                }
            } else {
                block();
            }
        }
    }

    private class WumpusWorldGamePerforming extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String message = msg.getContent();
                log.log(Level.SEVERE, "Wumpus world: Current world state before the action:");
                log.log(Level.SEVERE, cave.toString());

                boolean sendTerminateMessage = false;
                boolean sendWinMessage = false;

                switch (message) {
                    case AppConstant.SPELEOLOGIST_TURN_LEFT:
                        turnLeft();
                        break;
                    case AppConstant.SPELEOLOGIST_TURN_RIGHT:
                        turnRight();
                        break;
                    case AppConstant.SPELEOLOGIST_MOVE_FORWARD:
                        sendTerminateMessage = moveForward();
                        break;
                    case AppConstant.SPELEOLOGIST_GRAB:
                        grab();
                        break;
                    case AppConstant.SPELEOLOGIST_SHOOT:
                        shoot();
                        break;
                    case AppConstant.SPELEOLOGIST_CLIMB:
                        if (climb()) sendWinMessage = true;
                        else sendTerminateMessage = true;
                        break;
                    default:
                        log.log(Level.SEVERE, "Wumpus world: Wrong action!");
                        break;
                }

                log.log(Level.SEVERE, "Wumpus world: Current world state after the action:");
                log.log(Level.SEVERE, cave.toString());

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                if (!sendTerminateMessage)
                    reply.setContent(AppConstant.OK_MESSAGE);
                else if (sendWinMessage) {
                    reset();
                    reply.setContent(AppConstant.WIN_MESSAGE);
                } else {
                    reset();
                    reply.setContent(AppConstant.FAIL_MESSAGE);
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
