import axios from "axios";

const baseConfig = {
    baseURL: 'http://localhost:8080',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    withCredentials: true
};

export const publicApi = axios.create(baseConfig);

export const privateApi = axios.create(baseConfig);

export const uploadApi = axios.create({
    ...baseConfig,
    headers: {
        'Content-Type': 'multipart/form-data'
    },
    timeout: 30000 // Tăng timeout cho upload
});

export const thirdPartyApi = axios.create({
    ...baseConfig,
    baseURL: process.env.REACT_APP_THIRD_PARTY_URL
});

privateApi.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

const errorInterceptor = (error) => {
    if (error.response) {
        switch (error.response.status) {
            case 401:
                localStorage.removeItem('token');
                window.location.href = '/login';
                break;
            case 403:
                console.error('Không có quyền truy cập');
                break;
            case 404:
                console.error('Không tìm thấy tài nguyên');
                break;
            default:
                console.error('Lỗi:', error.response.data);
        }
    }
    return Promise.reject(error);
};

[publicApi, privateApi, uploadApi, thirdPartyApi].forEach(instance => {
    instance.interceptors.response.use(
        response => response,
        errorInterceptor
    );
});

export default {
    publicApi,
    privateApi,
    uploadApi,
    thirdPartyApi
};