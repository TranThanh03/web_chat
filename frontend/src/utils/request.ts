import axiosInstance from "./axiosInstance";
import type { ApiResponse } from "./apiResponse";

export const get = async <T>(url: string, params?: any): Promise<ApiResponse<T>> => {
    const res = await axiosInstance.get(url, { params });
    return res.data;
};

export const post = async <T>(url: string, data?: any): Promise<ApiResponse<T>> => {
    const res = await axiosInstance.post(url, data);
    return res.data;
};

export const patch = async <T>(url: string, data?: any): Promise<ApiResponse<T>> => {
    const res = await axiosInstance.patch(url, data);
    return res.data;
};

export const del = async <T>(url: string): Promise<ApiResponse<T>> => {
    const res = await axiosInstance.delete(url);
    return res.data;
};