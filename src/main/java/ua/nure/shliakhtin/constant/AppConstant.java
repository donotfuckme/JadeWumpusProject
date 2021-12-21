package ua.nure.shliakhtin.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstant {

    public static final String WUMPUS_WORLD_TYPE = "wumpus_world";
    public static final String WUMPUS_SERVICE_DESCRIPTION = "wumpus_world";
    public static final String NAVIGATOR_SERVICE_DESCRIPTION = "navigator";
    public static final String GO_INSIDE = "go_inside";
    public static final String WUMPUS_WORLD_DIGGER_CONVERSATION_ID = "digger_world";
    public static final String NAVIGATOR_DIGGER_CONVERSATION_ID = "digger_navigator";
    public static final String NAVIGATOR_AGENT_TYPE = "navigator_agent";
    public static final String INITIAL_WUMPUS_CAVE = ". . . P W G . . . . . . S . P . ";
    public static final String OK_MESSAGE = "OK";
    public static final String FAIL_MESSAGE = "FAIL";
    public static final String WIN_MESSAGE = "WIN";
    public static final String SPELEOLOGIST_TURN_LEFT = "SPELEOLOGIST_LOOK_LEFT";
    public static final String SPELEOLOGIST_TURN_RIGHT = "SPELEOLOGIST_TURN_RIGHT";
    public static final String SPELEOLOGIST_MOVE_FORWARD = "SPELEOLOGIST_MOVE_FORWARD";
    public static final String SPELEOLOGIST_GRAB = "SPELEOLOGIST_GRAB";
    public static final String SPELEOLOGIST_SHOOT = "SPELEOLOGIST_SHOOT";
    public static final String SPELEOLOGIST_CLIMB = "SPELEOLOGIST_CLIMB";
    public static final String GAME_INFORMATION = "INFORMATION";

    public static final String ADVICE_PROPOSAL = "Advice me, navigator.";
    public static final String INFORMATION_PROPOSAL_NAVIGATOR = "Give me current game information.";
    public static final String INFORMATION_PROPOSAL_SPELEOLOGIST = "Giving you information: ";
    public static final String ACTION_PROPOSAL1 = "You should ";
    public static final String ACTION_PROPOSAL2 = "I think it is a good option to ";
    public static final String ACTION_PROPOSAL3 = "Maybe you can ";

    public static final String MESSAGE_LEFT = "turn left.";
    public static final String MESSAGE_RIGHT = "turn right.";
    public static final String MESSAGE_FORWARD = "move forward.";
    public static final String MESSAGE_GRAB = "grab the gold.";
    public static final String MESSAGE_SHOOT = "shoot.";
    public static final String MESSAGE_CLIMB = "climb the ladder.";
}
