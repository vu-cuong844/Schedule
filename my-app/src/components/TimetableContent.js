import React, { useState } from 'react';
import * as XLSX from 'xlsx';
import '../assets/styles/TimetableContent.css';
import axios from 'axios';
import { saveAs } from 'file-saver';

const TimetableContent = () => {
    // State để lưu file và kỳ học
    const [classFile, setClassFile] = useState(null);
    const [term, setTerm] = useState('');
    const [timetableEntries, setTimetableEntries] = useState([]);
    const [isScheduling, setIsScheduling] = useState(false);


    // Map số ngày thành tên thứ
    const mapDay = (day) => {
        const days = ['Chủ nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
        return days[day] || day;
    };

    // Map loại lớp
    const mapType = (type) => {
        switch (type) {
            case 'LY_THUYET': return 'Lý thuyết';
            case 'THUC_HANH': return 'Thực hành';
            case 'GD': return 'Giảng dạy';
            case 'TN': return 'Thực nghiệm';
            default: return type;
        }
    };

    // Xử lý khi chọn file
    const handleClassFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setClassFile(file);
        }
    };

    // Xử lý khi nhấn nút "Lập lịch"
    const handleSchedule = async () => {
        if (!classFile || !term) {
            alert('Vui lòng chọn file lớp và nhập kỳ học!');
            return;
        }

        try {
            // Đọc file Excel
            const classData = await readExcelFile(classFile);

            // Chuyển dữ liệu lớp thành định dạng JSON
            const classes = classData.map(row => ({
                id: row['Mã_lớp']?.toString() || '',
                maHP: row['Mã_HP']?.toString() || '',
                slMax: parseInt(row['SL_Max']) || 0,
                thoiLuong: parseInt(row['Thời_lượng']) || 0,
                type: row['Loại_lớp']?.toUpperCase() || ''
            }));

            // Tạo body theo định dạng yêu cầu
            const body = {
                classes,
                teachers: null,
                rooms: null,
                term: parseInt(term) || 0
            };

            console.log('Body:', body.classes.length);
            setIsScheduling(true); // Bắt đầu lập lịch

            const response = await axios.post(
                'http://localhost:8080/api/schedule/',
                body,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    withCredentials: true  // Bắt buộc gửi cookie (như access_token) kèm request
                }
            );

            console.log('Kết quả schedule:', response);

            setTimetableEntries(response.data.data.items)
        } catch (error) {
            console.error('Lỗi khi xử lý file:', error);
            alert('Đã có lỗi xảy ra khi xử lý file!');
        } finally {
            setIsScheduling(false); // Dù thành công hay lỗi, cũng kết thúc trạng thái "đang lập lịch"
        }
    };

    // Hàm đọc file Excel
    const readExcelFile = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const data = new Uint8Array(e.target.result);
                    const workbook = XLSX.read(data, { type: 'array' });
                    const sheetName = workbook.SheetNames[0];
                    const worksheet = workbook.Sheets[sheetName];
                    const json = XLSX.utils.sheet_to_json(worksheet);
                    resolve(json);
                } catch (error) {
                    reject(error);
                }
            };
            reader.onerror = (error) => reject(error);
            reader.readAsArrayBuffer(file);
        });
    };

    const handleDownloadExcel = async () => {
        if (timetableEntries.length === 0) {
            alert('Chưa có dữ liệu để tải!');
            return;
        }

        try {
            // 1. Gửi xác nhận lập lịch
            const confirmBody = {
                items: timetableEntries,
                term: parseInt(term)
            };

            const response = await axios.post(
                'http://localhost:8080/api/schedule/confirm',
                confirmBody,
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true
                }
            );

            console.log(response);

            // 2. Chuyển dữ liệu sang định dạng Excel
            const headers = [
                ['ID Lớp', 'Mã môn', 'Tên môn', 'Tên môn (Tiếng Anh)', 'Tín chỉ', 'Ngày', 'Giờ bắt đầu', 'Giờ kết thúc',
                    'Bắt đầu', 'Kết thúc', 'Buổi', 'Tuần học', 'Phòng', 'Yêu cầu TN', 'SL_Max',
                    'Loại lớp', 'Mã quản lý', 'Hình thức giảng dạy', 'Tên giáo viên', 'Mã giáo viên', 'Học kỳ']
            ];

            // Convert data để khớp thứ tự với headers
            const data = timetableEntries.map((entry) => [
                entry.idClass,
                entry.idSubject,
                entry.nameSubject,
                entry.nameEnglishSubject,
                entry.weight,
                mapDay(entry.day),
                entry.timeStart,
                entry.timeENd,
                entry.start,
                entry.end,
                entry.session,
                entry.weeks.join(', '),
                entry.room,
                entry.requetTN ? 'Có' : 'Không',
                entry.slMax,
                mapType(entry.type),
                entry.managementCode,
                entry.teachingType,
                entry.teacherName,
                entry.teacherCode,
                entry.term
            ]);

            const worksheet = XLSX.utils.aoa_to_sheet([...headers, ...data]);

            const workbook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(workbook, worksheet, 'Schedule');

            // 3. Xuất file Excel
            const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
            const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });

            saveAs(blob, `Thoi_khoa_bieu_HK${term}.xlsx`);
        } catch (error) {
            console.error('Lỗi khi tải file Excel:', error);
            alert('Đã xảy ra lỗi khi tải file Excel!');
        }
    };

    return (
        <div className="timetable-content">
            <h3>Thời khóa biểu</h3>
            <div className="file-upload-group">
                <div className="file-upload">
                    <label htmlFor="class-upload">Lớp:</label>
                    <input
                        type="file"
                        id="class-upload"
                        accept=".xlsx, .xls"
                        className="input-file"
                        onChange={handleClassFileChange}
                    />
                </div>
                <div className="file-upload">
                    <label htmlFor="term-input">Kỳ học:</label>
                    <input
                        type="number"
                        id="term-input"
                        className="input-file"
                        value={term}
                        onChange={(e) => setTerm(e.target.value)}
                    />
                </div>
            </div>
            <div className="button-group">
                <button className="download-button" onClick={handleDownloadExcel}>
                    Tải file Excel
                </button>
                <button
                    className="schedule-button"
                    onClick={handleSchedule}
                    disabled={isScheduling}
                >
                    {isScheduling ? 'Đang lập lịch...' : 'Lập lịch'}
                </button>

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
                        {timetableEntries.map((entry) => (
                            <tr key={entry.idClass}>
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
                                <td>{entry.weeks.join(', ')}</td>
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
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default TimetableContent;