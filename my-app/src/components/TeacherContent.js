import React, { useState, useEffect } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import '../assets/styles/TeacherContent.css';

const TeacherContent = () => {
    const [teachers, setTeachers] = useState([]);
    const [transformedTeachers, setTransformedTeachers] = useState([]);

    useEffect(() => {
        fetchTeachers();
    }, []);

    const fetchTeachers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/schedule/teacher/', {
                headers: {
                    'Content-Type': 'application/json'
                },
                withCredentials: true
            });
            setTeachers(response.data);
        } catch (error) {
            console.error('Error fetching teachers:', error);
        }
    };

    const handleFileUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = async (e) => {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: 'array' });
            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];
            const jsonData = XLSX.utils.sheet_to_json(worksheet);

            const transformed = jsonData.map(row => ({
                username: row['Username'],
                email: row['Email'],
                password: row['Password'],
                confirmPassword: row['ConfirmPassword'],
                role: row['Role'],
                teacher: {
                    teacherCode: null,
                    name: row['Name'],
                    subjectCodes: JSON.parse(row['HP'].replace(/'/g, '"')),
                    type: JSON.parse(row['Type'].replace(/'/g, '"')),
                    hoc_vi: row['Học vị'],
                    time: parseInt(row['Time']),
                    priority_gd: null,  // mặc định
                    priority_tn: null,  // mặc định
                    institute: row['Đơn vị'],
                    department: row['Phòng ban']
                },
                provider: null,
                providerId: null,
                providerToken: null
            }));

            console.log('Transformed Teachers:', transformed);
            setTransformedTeachers(transformed);
        };

        reader.readAsArrayBuffer(file);
    };

    const handleAddTeachers = async () => {
        if (transformedTeachers.length === 0) {
            alert('Chưa có dữ liệu để thêm.');
            return;
        }

        try {
            await axios.post(
                'http://localhost:8080/api/auth/register', 
                transformedTeachers,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    withCredentials: true
                }
            );

            alert('Thêm giáo viên thành công!');
            fetchTeachers(); // Refresh lại danh sách từ server
        } catch (error) {
            console.error('Lỗi khi thêm giáo viên:', error);
            alert('Có lỗi xảy ra khi thêm giáo viên.');
        }
    };

    return (
        <div className="teacher-content">
            <h3>Danh sách giáo viên</h3>
            <input
                type="file"
                accept=".xlsx, .xls"
                className="input-file"
                onChange={handleFileUpload}
            />
            <div className="content-table-container">
                <table className="content-table">
                    <thead>
                        <tr>
                            <th>Mã giáo viên</th>
                            <th>Tên giáo viên</th>
                            <th>Môn học</th>
                            <th>Loại hợp đồng</th>
                            <th>Học vị</th>
                            <th>Thời gian (giờ)</th>
                            <th>Trường/Viện</th>
                            <th>Khoa</th>
                        </tr>
                    </thead>
                    <tbody>
                        {teachers.map((teacher) => (
                            <tr key={teacher.teacherCode}>
                                <td>{teacher.teacherCode}</td>
                                <td>{teacher.name}</td>
                                <td>{Array.isArray(teacher.subjectCodes) ? teacher.subjectCodes.join(', ') : ''}</td>
                                <td>{Array.isArray(teacher.type) ? teacher.type.join(', ') : ''}</td>
                                <td>{teacher.hoc_vi}</td>
                                <td>{teacher.time}</td>
                                <td>{teacher.institute}</td>
                                <td>{teacher.department}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button className="add-button" onClick={handleAddTeachers}>
                Thêm giáo viên
            </button>
        </div>
    );
};

export default TeacherContent;
