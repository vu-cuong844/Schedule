import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; // Đảm bảo vẫn giữ dòng này nếu bạn dùng getRole()

export const setToken = (token) => {
  localStorage.setItem('token', token);
};

export const getToken = () => {
  return localStorage.getItem('token');
};

export const getRole = () => {
  const token = getToken();
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.roles;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
  return null;
};

export const logout = async () => {
  try {
    await axios.post('http://localhost:8080/api/auth/logout', null, {
      withCredentials: true // Nếu backend xài HttpOnly cookies
    });
  } catch (error) {
    console.error('Lỗi khi gọi API logout:', error);
  } finally {
    localStorage.removeItem('token'); // Dù API có lỗi vẫn đảm bảo xoá token local
  }
};
