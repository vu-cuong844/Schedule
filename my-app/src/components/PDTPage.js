import React from 'react';
import { Routes, Route, NavLink, useNavigate } from 'react-router-dom';
import { logout } from '../utils/auth';
import logo from '../assets/images/logo.png';
import '../assets/styles/PDTPage.css';
import TeacherContent from './TeacherContent';
import RoomContent from './RoomContent';
import ClassContent from './ClassContent';
import TimetableContent from './TimetableContent';
import SubjectContent from './SubjectContent';
import { Await } from 'react-router-dom';

const PDTPage = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="pdt-page">
      <header className="pdt-header">
        <img src={logo} alt="Logo" className="header-logo" />
        <button className="logout-button" onClick={handleLogout}>
          Đăng xuất
        </button>
      </header>
      <div className="pdt-container">
        <nav className="pdt-menu">
          <ul>
            <li>
              <NavLink to="/pdt/teacher" className={({ isActive }) => (isActive ? 'active' : '')}>
                Teacher
              </NavLink>
            </li>
            <li>
              <NavLink to="/pdt/room" className={({ isActive }) => (isActive ? 'active' : '')}>
                Room
              </NavLink>
            </li>
            <li>
              <NavLink to="/pdt/class" className={({ isActive }) => (isActive ? 'active' : '')}>
                Class
              </NavLink>
            </li>
            <li>
              <NavLink to="/pdt/timetable" className={({ isActive }) => (isActive ? 'active' : '')}>
                Timetable
              </NavLink>
            </li>
            <li>
              <NavLink to="/pdt/subject" className={({ isActive }) => (isActive ? 'active' : '')}>
                Subject
              </NavLink>
            </li>
          </ul>
        </nav>
        <main className="pdt-content">
          <Routes>
            <Route path="teacher" element={<TeacherContent />} />
            <Route path="room" element={<RoomContent />} />
            <Route path="class" element={<ClassContent />} />
            <Route path="timetable" element={<TimetableContent />} />
            <Route path="subject" element={<SubjectContent />} /> {/* ✅ route mới */}
            <Route path="/" element={<TeacherContent />} />
          </Routes>
        </main>
      </div>
      <footer className="pdt-footer">
        <p>&copy; 2025 Quản lý Giáo dục. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default PDTPage;
