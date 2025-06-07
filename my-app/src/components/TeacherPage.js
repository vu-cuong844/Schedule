import React from 'react';
import { Routes, Route, NavLink, useNavigate, Navigate } from 'react-router-dom';
import { logout } from '../utils/auth';
import logo from '../assets/images/logo.png';
import '../assets/styles/TeacherPage.css'; // CSS riêng cho trang teacher
//import TeacherProfile from './TeacherProfile';
import TimetableContent from './TimeableContent';

const TeacherPage = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="teacher-page">
      <header className="teacher-header">
        <img src={logo} alt="Logo" className="header-logo" />
        <button className="logout-button" onClick={handleLogout}>
          Đăng xuất
        </button>
      </header>

      <div className="teacher-container">
        <nav className="teacher-menu">
          <ul>
            <li>
              <NavLink to="/teacher/profile" className={({ isActive }) => (isActive ? 'active' : '')}>
                Hồ sơ
              </NavLink>
            </li>
            <li>
              <NavLink to="/teacher/timetable" className={({ isActive }) => (isActive ? 'active' : '')}>
                Thời khóa biểu
              </NavLink>
            </li>
          </ul>
        </nav>

        <main className="teacher-content">
          <Routes>
            {/* <Route path="profile" element={<TeacherProfile />} /> */}
            <Route path="timetable" element={<TimetableContent />} />
            <Route path='/' element={<TimetableContent/>} />
          </Routes>
        </main>
      </div>

      <footer className="teacher-footer">
        <p>&copy; 2025 Quản lý Giáo dục. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default TeacherPage;
