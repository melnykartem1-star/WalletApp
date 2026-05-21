import axios from 'axios';
import { toast } from 'react-toastify';

const API_URL = 'http://localhost:8082/api/v1';

export const axiosInstance = axios.create({
    baseURL: API_URL,
});

axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => Promise.reject(error));

axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Автоматичний рефреш токен (залишаємо як було)
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) throw new Error("No refresh token");

                const res = await axios.post(`${API_URL}/auth/refresh`, { refreshToken });
                localStorage.setItem('accessToken', res.data.accessToken);
                if (res.data.refreshToken) localStorage.setItem('refreshToken', res.data.refreshToken);

                originalRequest.headers.Authorization = `Bearer ${res.data.accessToken}`;
                return axiosInstance(originalRequest);
            } catch (refreshError) {
                localStorage.clear();
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        // Глобальна обробка помилок для UI
        if (error.response && error.response.status !== 401) {
            const message = error.response.data?.message || 'Сталася помилка на сервері';
            toast.error(message);
        } else if (!error.response) {
            toast.error('Помилка з\'єднання з сервером');
        }

        return Promise.reject(error);
    }
);