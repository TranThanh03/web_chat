import { API_ENDPOINT } from "@/config/apiEndpoint";
import { post } from "@/utils/request";

const AuthService = {
    login: (data: any) => {
        return post<any>(API_ENDPOINT.AUTH.LOGIN_LOCAL, data);
    },
    loginOAuth: (data: any) => {
        return post<any>(API_ENDPOINT.AUTH.LOGIN_OAUTH, data);
    },
};

export default AuthService;