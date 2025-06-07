import React, { useState } from 'react';
import '../assets/styles/TeacherTimetable.css';
import axios from 'axios';

const TeacherTimetable = () => {
    const [semester, setSemester] = useState('');
    const [timetableData, setTimetableData] = useState([]);

    const handleSemesterChange = (e) => {
        setSemester(e.target.value);
    };


    const handleSearch = async () => {
        if (!semester) {
            alert('Vui lòng nhập kỳ học');
            return;
        }

        try {
            const response = await axios.get('http://localhost:8080/api/schedule/timetable', {
                params: { term: semester },
                withCredentials: true,
            });
            console.log(response);
            // Thường response.data sẽ là object Response<TimeTable>
            setTimetableData(response.data.data.items);
        } catch (error) {
            console.error('Lỗi khi gọi API:', error);
            alert('Không lấy được dữ liệu thời khóa biểu. Vui lòng thử lại.');
        }
    };


    // Hàm chuyển số thứ thành tên thứ (copy từ TimetableContent)
    const mapDay = (day) => {
        const days = ['Chủ nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
        return days[day] || day;
    };

    // Hàm map loại lớp (copy từ TimetableContent)
    const mapType = (type) => {
        switch (type) {
            case 'LY_THUYET': return 'Lý thuyết';
            case 'THUC_HANH': return 'Thực hành';
            case 'GD': return 'Giảng dạy';
            case 'TN': return 'Thực nghiệm';
            default: return type;
        }
    };

    return (
        <div className="teacher-timetable">
            <h2>Thời khóa biểu của tôi</h2>
            <div className="timetable-controls">
                <label htmlFor="semester">Kỳ:</label>
                <input
                    type="number"
                    id="semester"
                    value={semester}
                    onChange={handleSemesterChange}
                    min="1"
                    placeholder="Nhập kỳ"
                />
                <button onClick={handleSearch}>Tìm</button>
            </div>

            <div className="table-wrapper">
                <table className="content-table">
                    <thead>
                        <tr>
                            <th>ID Lớp</th>
                            <th>Mã môn</th>
                            <th>Tên môn</th>
                            <th>Tên môn (Tiếng Anh)</th>
                            <th>Tín chỉ</th>
                            <th>Ngày</th>
                            <th>Giờ bắt đầu</th>
                            <th>Giờ kết thúc</th>
                            <th>Tuần bắt đầu</th>
                            <th>Tuần kết thúc</th>
                            <th>Buổi</th>
                            <th>Tuần học</th>
                            <th>Phòng</th>
                            <th>Yêu cầu TN</th>
                            <th>Sức chứa</th>
                            <th>Loại lớp</th>
                            <th>Mã quản lý</th>
                            <th>Hình thức giảng dạy</th>
                            <th>Tên giáo viên</th>
                            <th>Mã giáo viên</th>
                            <th>Học kỳ</th>
                        </tr>
                    </thead>
                    <tbody>
                        {timetableData.length === 0 ? (
                            <tr>
                                <td colSpan="21" style={{ textAlign: 'center' }}>
                                    Chưa có dữ liệu
                                </td>
                            </tr>
                        ) : (
                            timetableData.map((entry, index) => (
                                <tr key={index}>
                                    <td>{entry.idClass}</td>
                                    <td>{entry.idSubject}</td>
                                    <td>{entry.nameSubject}</td>
                                    <td>{entry.nameEnglishSubject}</td>
                                    <td>{entry.weight}</td>
                                    <td>{mapDay(entry.day)}</td>
                                    <td>{entry.timeStart}</td>
                                    <td>{entry.timeENd}</td>
                                    <td>{entry.start}</td>
                                    <td>{entry.end}</td>
                                    <td>{entry.session}</td>
                                    <td>{entry.weeks?.join(', ')}</td>
                                    <td>{entry.room}</td>
                                    <td>{entry.requetTN ? 'Có' : 'Không'}</td>
                                    <td>{entry.slMax}</td>
                                    <td>{mapType(entry.type)}</td>
                                    <td>{entry.managementCode}</td>
                                    <td>{entry.teachingType}</td>
                                    <td>{entry.teacherName}</td>
                                    <td>{entry.teacherCode}</td>
                                    <td>{entry.term}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default TeacherTimetable;
