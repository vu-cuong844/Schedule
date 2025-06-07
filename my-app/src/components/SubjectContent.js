import React, { useState, useEffect } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import '../assets/styles/RoomContent.css'; // dùng chung style với RoomContent

const SubjectContent = () => {
    const [subjects, setSubjects] = useState([]);
    const [pendingSubjects, setPendingSubjects] = useState([]);

    useEffect(() => {
        fetchSubjects();
    }, []);

    const fetchSubjects = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/schedule/subject/', {
                headers: { 'Content-Type': 'application/json' },
                withCredentials: true
            });
            // console.log(response.data.data)
            setSubjects(response.data.data);
        } catch (error) {
            console.error('Lỗi lấy danh sách môn học:', error);
        }
    };

    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e) => {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: 'array' });
            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];
            const jsonData = XLSX.utils.sheet_to_json(worksheet);

            const transformed = jsonData.map(row => ({
                subjectCode: row['Mã_HP'],
                nameSubject: row['Tên_HP'],
                nameEnglishSubject: row['Tên_HP_Tiếng_Anh'],
                count: Number(row['Khối_lượng']),
                lt: Number(row['LT']),
                bt: Number(row['BT']),
                tn: Number(row['TN']),
                tuhoc: Number(row['TH']),
                requestTN: row['Cần_TN'] ? true : false
            }));

            console.log('Dữ liệu import:', transformed);
            setPendingSubjects(transformed);
        };
        reader.readAsArrayBuffer(file);
    };

    const handleAddSubjects = async () => {
        if (pendingSubjects.length === 0) {
            alert("Chưa có dữ liệu môn học để thêm.");
            return;
        }

        try {
            await axios.post(
                'http://localhost:8080/api/schedule/subject/',
                {
                    subjects: pendingSubjects
                },
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true
                }
            );
            alert('Thêm môn học thành công!');
            await fetchSubjects();
            setPendingSubjects([]);
        } catch (error) {
            console.error('Lỗi khi thêm môn học:', error);
        }
    };

    return (
        <div className="room-content">
            <h3>Danh sách môn học</h3>
            <input
                type="file"
                accept=".xlsx, .xls"
                className="input-file"
                onChange={handleFileUpload}
            />
            <div className='content-table-container'>
                <table className="content-table">
                    <thead>
                        <tr>
                            <th>Mã HP</th>
                            <th>Tên HP</th>
                            <th>Tên tiếng Anh</th>
                            <th>Khối lượng (Tín chỉ)</th>
                            <th>LT</th>
                            <th>BT</th>
                            <th>TN</th>
                            <th>Tự học</th>
                            <th>Cần TN</th>
                        </tr>
                    </thead>
                    <tbody>
                        {subjects.map((subject, index) => (
                            <tr key={index}>
                                <td>{subject.subjectCode}</td>
                                <td>{subject.nameSubject}</td>
                                <td>{subject.nameEnglishSubject}</td>
                                <td>{subject.count}</td>
                                <td>{subject.lt}</td>
                                <td>{subject.bt}</td>
                                <td>{subject.tn}</td>
                                <td>{subject.tuhoc}</td>
                                <td>{subject.requestTN ? '✔️' : ''}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button className="add-button" onClick={handleAddSubjects}>
                Thêm môn học
            </button>
        </div>
    );
};

export default SubjectContent;
