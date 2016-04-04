package uno.cod.platform.server.rest;


public class RestUrls {
    public static final String CHALLENGE_TEMPLATES = "/challenges/templates";
    public static final String CHALLENGE_TEMPLATES_ID = "/challenges/templates/{id}";
    public static final String CHALLENGE_ID_TEMPLATE = "/challenges/{id}/template";
    public static final String CHALLENGE_TEMPLATES_ID_CHALLENGE = "/challenges/templates/{id}/challenges";

    public static final String CHALLENGES_ID = "/challenges/{id}";
    public static final String CHALLENGES = "/challenges";
    public static final String CHALLENGES_ID_INVITATIONS = "/challenges/{id}/invitations";


    public static final String ORGANIZATIONS = "/organizations";
    public static final String ORGANIZATIONS_ID = "/organizations/{id}";

    public static final String ORGANIZATION_MEMBERS = "/organization/{id}/members";

    public static final String INVITE = "/invite";
    public static final String INVITE_AUTH_TOKEN = "/invite/auth/{token}";


    public static final String USERS = "/users";
    public static final String USER = "/user";
    public static final String USER_ORGANIZATION = "/user/organization";

    public static final String RESULTS = "/results";
    public static final String RESULTS_ID = "/results/{id}";
    public static final String RESULTS_CHALLENGE_ID_MY = "/results/challenge/{id}/my";
    public static final String RESULTS_ID_TASK_ID = "/results/{id}/task/{taskId}";

    public static final String LEADERBOARD = "/challenge/{id}/leaderboard";

    public static final String TASKS = "/tasks";
    public static final String TASKS_ID = "/tasks/{id}";

    public static final String TEMPLATES = "/templates";
    public static final String TEMPLATES_ID = "/templates/{id}";

    public static final String TASKS_ID_TESTS = "/tasks/{id}/tests";

    public static final String RESULTS_TASKS_SUBMISSIONS = "/results/{resultId}/tasks/{taskId}/submissions";
    public static final String RESULTS_TESTS_OUTPUT = "/results/{resultId}/task/{taskId}/output";
    public static final String TASKS_ID_RUN = "/tasks/{taskId}/run";

    public static final String SETUP = "/setup";

    public static final String ENDPOINTS = "/endpoints";
    public static final String RUNNERS = "/runners";
    public static final String LANGUAGES = "/languages";

    public static final String TESTS = "/tests";

}
