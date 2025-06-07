import React from 'react';
import '../assets/styles/ClassContent.css';

const ClassContent = () => {
    // Dữ liệu mẫu theo model bạn đưa
    const classes = [
        {
            id: '1',
            maHP: 'Lớp 10A1',
            slMax: 40,
            thoiLuong: 10,  // ví dụ 10 tuần/học kỳ
            type: 'LY_THUYET'
        },
        {
            id: '2',
            maHP: 'Lớp 11B2',
            slMax: 35,
            thoiLuong: 12,
            type: 'THUC_HANH'
        }
    ];

    // Hàm chuyển type enum thành chuỗi dễ hiểu (tuỳ chỉnh theo bạn)
    const mapClassType = (type) => {
        switch (type) {
            case 'LY_THUYET': return 'Lý thuyết';
            case 'THUC_HANH': return 'Thực hành';
            default: return type;
        }
    };

    return (
        <div className="class-content">
            <h3>Danh sách lớp học</h3>
            <input type="file" accept=".xlsx, .xls" />
            <div className='content-table-container'>
                <table className="content-table">
                    <thead>
                        <tr>
                            <th>Mã lớp học</th>
                            <th>Mã học phần</th>
                            <th>Sức chứa tối đa</th>
                            <th>Thời lượng (tiết)</th>
                            <th>Loại lớp</th>
                        </tr>
                    </thead>
                    <tbody>
                        {classes.map((cls) => (
                            <tr key={cls.id}>
                                <td>{cls.id}</td>
                                <td>{cls.maHP}</td>
                                <td>{cls.slMax}</td>
                                <td>{cls.thoiLuong}</td>
                                <td>{mapClassType(cls.type)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ClassContent;
