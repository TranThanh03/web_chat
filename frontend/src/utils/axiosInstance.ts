import axios, { AxiosError } from "axios";
import type { AxiosInstance, InternalAxiosRequestConfig } from "axios";

const axiosInstance: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_BASE_BE_URL,
    timeout: 20000,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
});

let accessToken: string | null = null;

export const setAccessToken = (token: string | null): void => {
    accessToken = token;
};

axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error: AxiosError) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error: AxiosError) => {
        // if ((navigator.onLine && error.code === "ERR_NETWORK") || error.response?.status === 500) {
        //     window.location.href = "/error/500";
        // }

        return Promise.reject(error.response || error.message);
    }
);

export default axiosInstance;