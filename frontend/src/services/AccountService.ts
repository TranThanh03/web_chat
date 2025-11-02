import { API_ENDPOINT } from "@/config/apiEndpoint";
import { post } from "@/utils/request";

const AccountService = {
    register: (data: any) => {
        return post<any>(API_ENDPOINT.ACCOUNT.REGISTER, data);
    },
};

export default AccountService;