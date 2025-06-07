import React, { useState, useEffect } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import '../assets/styles/RoomContent.css';

const RoomContent = () => {
    const [rooms, setRooms] = useState([]);
    const [pendingRooms, setPendingRooms] = useState([]);

    useEffect(() => {
        fetchRooms();
    }, []);

    const fetchRooms = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/schedule/room/', {
                headers: {
                    'Content-Type': 'application/json'
                },
                withCredentials: true
            });
            console.log(response.data)
            setRooms(response.data);
        } catch (error) {
            console.error('Error fetching rooms:', error);
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

            const transformedData = jsonData.map(row => ({
                name: row['Phòng'],
                subjectCodes: row['Mã_HP'] ? JSON.parse(row['Mã_HP'].replace(/'/g, '"')) : [],
                slMax: row['SL_Max'],
                type: row['Loại_lớp']
            }));
            console.log(transformedData)
            setPendingRooms(transformedData);
        };

        reader.readAsArrayBuffer(file);
    };

    const handleAddRooms = async () => {
        if (pendingRooms.length === 0) {
            alert("Chưa có dữ liệu phòng để thêm.");
            return;
        }

        try {
            await axios.post(
                'http://localhost:8080/api/schedule/room/',
                pendingRooms,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    withCredentials: true
                }
            );
            alert('Thêm phòng thành công!');
            await fetchRooms();
            setPendingRooms([]);
        } catch (error) {
            console.error('Error uploading rooms:', error);
        }
    };

    return (
        <div className="room-content">
            <h3>Danh sách phòng học</h3>
            <input
                type="file"
                accept=".xlsx, .xls"
                className='input-file'
                onChange={handleFileUpload}
            />
            <div className='content-table-container'>
                <table className="content-table">
                    <thead>
                        <tr>
                            <th>Tên phòng</th>
                            <th>Môn học</th>
                            <th>Sức chứa tối đa</th>
                            <th>Loại phòng</th>
                        </tr>
                    </thead>
                    <tbody>
                        {rooms.map((room, index) => (
                            <tr key={index}>
                                <td>{room.name}</td>
                                <td>{Array.isArray(room.subjectCodes) ? room.subjectCodes.join(', ') : ''}</td>
                                <td>{room.slMax}</td>
                                <td>{room.type}</td>
                            </tr>
                        ))}
                    </tbody>

                </table>
            </div>
            <button className="add-button" onClick={handleAddRooms}>
                Thêm phòng
            </button>
        </div>
    );
};

export default RoomContent;
