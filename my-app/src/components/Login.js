import React, { useState } from 'react';
import '../assets/styles/Login.css';
import logo from '../assets/images/logo.png';
import { authService } from '../services/authService';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const validateForm = () => {
        if (!formData.username.trim()) {
            setError('Vui lòng nhập tên đăng nhập');
            return false;
        }
        if (!formData.password) {
            setError('Vui lòng nhập mật khẩu');
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) return;

        console.log('Login attempt with:', {
            username: formData.username,
            password: formData.password
        });

        setIsLoading(true);
        setError('');

        try {
            const response = await authService.login(formData);
            console.log('User info:', response.user);

            if (response.accessToken) {
                localStorage.setItem('token', response.accessToken);
                if (response.user) {
                    localStorage.setItem('user', JSON.stringify(response.user));
                }

                const role = response.user?.role;
                switch (role) {
                    case 'PDT':
                        console.log("Tôi sẽ định tuyến bạn đến PDT")
                        navigate('/pdt/dashboard');
                        break;
                    case 'TEACHER':
                        console.log("Tôi sẽ định tuyến bạn đến TEACHER")
                        navigate('/teacher/dashboard');
                        break;
                    default:
                        navigate('/dashboard');
                }
            }
        } catch (err) {
            if (err.response?.status === 401) {
                setError('Tên đăng nhập hoặc mật khẩu không chính xác');
            } else if (err.response?.data?.message) {
                setError(err.response.data.message);
            } else {
                setError('Có lỗi xảy ra, vui lòng thử lại sau');
            }
            console.error('Login error:', err);
        } finally {
            setIsLoading(false);
        }

    };

    return (
        <div className="login-form">
            <div className="form-container">
                <div className="header-container">
                    <img src={logo} alt="Logo" className="logo" />
                    <h2>Đăng nhập</h2>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label htmlFor="username">Tên đăng nhập</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Nhập tên đăng nhập"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="password">Mật khẩu</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Nhập mật khẩu"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    {error && <p className="error">{error}</p>}
                    <button type="submit" disabled={isLoading}>
                        {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>
                </form>
                <Link
                    to="/forgot-password"
                    className="forgot-password-link"
                >
                    Quên mật khẩu?
                </Link>
            </div>
        </div>
    );
};

export default Login;