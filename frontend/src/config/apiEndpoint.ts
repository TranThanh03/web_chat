const API_VERSION = `/api/v1`;

export const API_ENDPOINT = {
    AUTH: {
        LOGIN_LOCAL: `${API_VERSION}/auth/login/local`,
        LOGIN_OAUTH: `${API_VERSION}/auth/login/oauth`,
        LOGOUT: `${API_VERSION}/auth/logout`,
    },
    ACCOUNT: {
        REGISTER: `${API_VERSION}/account/register`,
    },
    USER: {
        PROFILE: `${API_VERSION}/users/profile`,
        LIST: `${API_VERSION}/users`,
        DETAIL: (id: string) => `${API_VERSION}/users/${id}`,
    },
};