// src/components/ProtectedRoute.js
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRole }) => {
  const user = JSON.parse(localStorage.getItem('user'));
  const role = user?.role;
  console.log('ProtectedRoute - User:', user, 'Role:', role, 'Allowed:', allowedRole); // Debug

  if (!user || (allowedRole && role !== allowedRole)) {
    return <Navigate to="/login" />;
  }

  return children;
};

export default ProtectedRoute;