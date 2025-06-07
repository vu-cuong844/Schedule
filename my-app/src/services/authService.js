import { publicApi, privateApi } from "../api/axiosConfig";
import Login from "../components/Login";

export const authService = {
    // 1. login
    login: async (credentials) => {
        try {
            const response = await publicApi.post('/api/auth/login', credentials);
            
            if (response.data?.accessToken) {
                localStorage.setItem('token', response.data.accessToken);
            }
            
            return response.data;
        } catch (error) {
            console.error('Login Error Details:', {
                message: error.message,
                status: error.response?.status,
                statusText: error.response?.statusText,
                headers: error.response?.headers,
                config: error.config
            });
            throw error;
        }
    },

    //2. fogot pass
    forgotPassword: (email) => {
        return publicApi.post('/api/auth/forgot-password', email);
    },

    //3. reset pass
    resetPassword: (resetPassword) => {
        const resetBody = {
            email: resetPassword.email,
            OTP: resetPassword.OTP,
            newPassword: resetPassword.newPassword,
            confirmPassword: resetPassword.confirmPassword
        };
        return publicApi.post('/api/auth/reset-password', resetBody);
    },

    //4. refresh token
    refefreshToken: () => {
        return publicApi.post('/api/auth/refresh-token');
    },

    register: (users) => {
        const registerBody = users.map(user => ({
            username: user.username,
            email: user.email,
            password: user.password,
            confirmPassword: user.confirmPassword,
            role: user.role || null,
            teacher: {
                teacherCode: null,
                name: user.teacher.name,
                subjectCodes: user.teacher.subjectCodes,
                type: user.teacher.type,
                hoc_vi: user.teacher.hoc_vi,
                time: user.teacher.time,
                priority_gd: null,
                priority_tn: null,
                institute: user.teacher.institute,
                department: user.teacher.department
            },
            provider: null,
            providerId: null,
            providerToken: null
        }));

        return privateApi.post('/api/auth/register', registerBody);
    }
}