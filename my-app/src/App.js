// src/App.js
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import PDTPage from './components/PDTPage';
import TeacherPage from './components/TeacherPage';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<div>Trang Quên Mật Khẩu</div>} />
        <Route
          path="/pdt/*"
          element={
            <ProtectedRoute allowedRole="PDT">
              <PDTPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/teacher/*"
          element={
            <ProtectedRoute allowedRole="TEACHER">
              <TeacherPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute allowedRole={null}>
              <div>Trang Dashboard Mặc Định</div>
            </ProtectedRoute>
          }
        />
        <Route path="/" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;