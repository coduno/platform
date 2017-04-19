package uno.cod.platform.server.rest;


public class RestUrls {
    public static final String CHALLENGE_TEMPLATES = "/challenges/templates";
    public static final String CHALLENGE_TEMPLATES_CANONICAL_NAME = "/challenge-templates/{canonicalName}";
    public static final String CHALLENGE_TEMPLATES_CANONICAL_NAME_TASKS = "/challenge-templates/{canonicalName}/tasks";

    public static final String CHALLENGES = "/challenges";
    public static final String CHALLENGES_CANONICAL_NAME = "/challenges/{canonicalName}";
    public static final String CHALLENGES_CANONICAL_NAME_PARTICIPANTS = "/challenges/{canonicalName}/participants";
    public static final String CHALLENGES_CANONICAL_NAME_TEMPLATE = "/challenges/{canonicalName}/template";
    public static final String CHALLENGES_CANONICAL_NAME_REGISTER = "/challenges/{canonicalName}/register";
    public static final String CHALLENGES_CANONICAL_NAME_PARTICIPATION = "/challenges/{canonicalName}/participation";
    public static final String CHALLENGES_CANONICAL_NAME_INVITATIONS = "/challenges/{canonicalName}/invitations";
    public static final String CHALLENGES_CANONICAL_NAME_LOCATIONS = "/challenges/{canonicalName}/locations";
    public static final String CHALLENGES_PUBLIC = "/challenges/public";
    public static final String CHALLENGES_INVITE_ONLY = "/challenges/invite_only";

    public static final String ORGANIZATIONS = "/organizations";
    public static final String ORGANIZATIONS_ID = "/organizations/{id}";

    public static final String ORGANIZATION_MEMBERS = "/organization/{id}/members";

    public static final String INVITE = "/invite";
    public static final String INVITE_AUTH_TOKEN = "/invite/auth/{token}";

    public static final String USERS = "/users";
    public static final String USERS_USERNAME = "/users/{username}";
    public static final String USERS_USERNAME_TEAMS = "/users/{username}/teams";
    public static final String USERS_SEARCH = "/users/search";

    public static final String USER = "/user";
    public static final String USER_CHALLENGE_CANONICAL_NAME_STATUS = "/user/challenge/{canonicalName}/status";
    public static final String USER_PASSWORD = "/user/password";
    public static final String USER_PASSWORD_RESET = "/user/password/reset";
    public static final String USER_ORGANIZATIONS = "/user/organizations";
    public static final String USER_TEAMS = "/user/teams";
    public static final String USER_TEAMS_INVITATIONS = "/user/teams/invitations";

    public static final String USER_ACCCESSTOKEN = "/user/access_token";

    public static final String RESULTS = "/results";
    public static final String RESULTS_ID = "/results/{id}";
    public static final String RESULTS_CHALLENGE_ID_MY = "/results/challenge/{id}/my";
    public static final String RESULTS_ID_TASK_ID = "/results/{id}/task/{taskId}";

    public static final String LEADERBOARD = "/challenge/{id}/leaderboard";

    public static final String TASKS = "/tasks";
    public static final String TASKS_ID = "/task/{id}";

    public static final String TASKS_ID_TEMPLATES = "/tasks/{id}/templates";
    public static final String TASKS_ID_TEMPLATES_NAME = "/tasks/{id}/templates/{name}";

    public static final String TASKS_ID_TESTS = "/tasks/{id}/tests";

    public static final String RESULTS_TASKS_SUBMISSIONS = "/results/{resultId}/tasks/{taskId}/submissions";
    public static final String RESULTS_TESTS_OUTPUT = "/results/{resultId}/task/{taskId}/output";
    public static final String TASKS_ID_RUN = "/tasks/{taskId}/run";

    public static final String SETUP = "/setup";

    public static final String ENDPOINTS = "/endpoints";
    public static final String RUNNERS = "/runners";
    public static final String LANGUAGES = "/languages";

    public static final String TESTS = "/tests";

    public static final String TEAMS = "/teams";
    public static final String TEAMS_CANONICAL_NAME = "/teams/{canonicalName}";
    public static final String TEAMS_CANONICAL_NAME_INVITATION_ACCEPT = "/teams/{canonicalName}/invitation/accept";
    public static final String TEAMS_CANONICAL_NAME_INVITATION_DECLINE = "/teams/{canonicalName}/invitation/decline";
    public static final String TEAMS_CANONICAL_NAME_INVITATIONS = "/teams/{canonicalName}/invitations";

    public static final String LOCATIONS = "/locations";

    public static final String CATCODER_GAME_UPLOAD = "/contestgameupload";

    public static final String CONNECT = "/connect";
    public static final String CONNECT_PROVIDERID = "/connect/{providerId}";
    public static final String CONNECT_PROVIDERID_USERID = "/connect/{providerId}/{providerUserId}";

    public static final String PARTICIPATION_INVITATIONS = "/participation/invitations";
    public static final String PARTICIPATION_INVITATIONS_ACCEPT = "/participation/invitations/accept";

    public static final String ACTIVATION_TOKENS = "/activation_tokens";
}
