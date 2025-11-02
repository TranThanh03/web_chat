import { API_ENDPOINT } from "@/config/apiEndpoint";
import axiosInstance from "@/utils/axiosInstance";

const AccountService = {
    register: (data: any) => {
        return axiosInstance.post(API_ENDPOINT.ACCOUNT.REGISTER, data);
    },
};

export default AccountService;