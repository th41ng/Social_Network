

let currentOptionIndex = 0;


function initializeQuestionForm() {
    const optionsContainer = document.getElementById('optionsContainer');
    if (optionsContainer) {
        currentOptionIndex = optionsContainer.querySelectorAll('.option-entry').length;
    } else {
        currentOptionIndex = 0; // Mặc định nếu không có container (ví dụ: form mới tinh)
    }

    const questionTypeSelect = document.getElementById('selectedTypeId');
    const multipleChoiceSection = document.getElementById('multipleChoiceOptionsSection');

    function toggleOptionsVisibility() {
        if (!questionTypeSelect || !multipleChoiceSection) {
            console.warn("toggleOptionsVisibility: Không tìm thấy các phần tử cần thiết (selectedTypeId hoặc multipleChoiceOptionsSection).");
            return;
        }

        const currentSelectedValue = questionTypeSelect.value; // Lấy ID (value) của option được chọn
        let isMultipleChoiceType = false;

        console.log("Giá trị (ID) của loại câu hỏi được chọn:", currentSelectedValue);

        // ID của loại câu hỏi "Multiple Choice" là "1" (dưới dạng chuỗi vì value từ HTML select thường là chuỗi)
        const MULTIPLE_CHOICE_TYPE_ID_VALUE = "1";
        console.log("Kiểm tra với ID trắc nghiệm mục tiêu:", MULTIPLE_CHOICE_TYPE_ID_VALUE);

        if (currentSelectedValue === MULTIPLE_CHOICE_TYPE_ID_VALUE) {
            isMultipleChoiceType = true;
            console.log("ĐÃ KHỚP ID TRẮC NGHIỆM => isMultipleChoiceType = true");
        } else {
            console.log("KHÔNG KHỚP ID TRẮC NGHIỆM => isMultipleChoiceType = false");
        }

        if (isMultipleChoiceType) {
            console.log("=> Đang HIỆN multipleChoiceOptionsSection");
            multipleChoiceSection.style.display = 'block';
            // Logic thêm 'required' cho option đầu tiên nếu cần
            const firstOptionInput = optionsContainer.querySelector('input[name^="surveyOptions"][name$=".optionText"]');
            if (firstOptionInput && optionsContainer.querySelectorAll('.option-entry').length > 0) {
                // Bạn có thể quyết định có nên đặt required ở đây hay không,
                // hoặc khi thêm option mới trong hàm addOption.
                // firstOptionInput.required = true;
            }
        } else {
            console.log("=> Đang ẨN multipleChoiceOptionsSection");
            multipleChoiceSection.style.display = 'none';
            const allOptionInputs = multipleChoiceSection.querySelectorAll('input[name^="surveyOptions"][name$=".optionText"]');
            allOptionInputs.forEach(input => input.required = false);
        }
    }

    if (questionTypeSelect) {
        toggleOptionsVisibility(); // Gọi khi tải trang
        questionTypeSelect.addEventListener('change', toggleOptionsVisibility);
    } else {
        // console.warn("Không tìm thấy dropdown loại câu hỏi (selectedTypeId).");
    }
}

// Hàm này được gọi từ nút "Thêm lựa chọn" trong HTML
function addOption() {
    const optionsContainer = document.getElementById("optionsContainer"); // ID của div chứa các input option

    if (!optionsContainer) {
        console.error("Lỗi: Không tìm thấy vùng chứa các lựa chọn (phần tử với id='optionsContainer').");
        alert("Đã xảy ra lỗi khi cố gắng thêm lựa chọn. Vui lòng thử làm mới trang.");
        return;
    }

    const entryDiv = document.createElement('div');
    entryDiv.classList.add('option-entry', 'mb-2', 'd-flex', 'align-items-center');

    const textInput = document.createElement('input');
    textInput.type = 'text';
    textInput.classList.add('form-control');
    // QUAN TRỌNG: Thay đổi name để Spring MVC bind đúng
    textInput.name = `surveyOptions[${currentOptionIndex}].optionText`;
    textInput.placeholder = 'Nhập lựa chọn mới';

    // Xử lý 'required' cho input mới dựa trên loại câu hỏi hiện tại
    const questionTypeSelect = document.getElementById('selectedTypeId');
    if (questionTypeSelect) {
        const selectedOptionElement = questionTypeSelect.options[questionTypeSelect.selectedIndex];
        let isMultipleChoiceType = false;
        if (selectedOptionElement) {
            const selectedText = selectedOptionElement.text.toLowerCase();
            if (selectedText.includes("trắc nghiệm") || selectedText.includes("chọn một") || selectedText.includes("chọn nhiều")) {
                isMultipleChoiceType = true;
            }
        }
        if (isMultipleChoiceType) {
            textInput.required = true; // Hoặc false tùy theo logic của bạn
        }
    }


    const removeButton = document.createElement('button');
    removeButton.type = 'button';
    removeButton.classList.add('btn', 'btn-danger', 'btn-sm', 'ms-2');
    removeButton.textContent = 'Xóa';
    removeButton.onclick = function () {
        removeOptionEntry(this);
    };

    entryDiv.appendChild(textInput);
    entryDiv.appendChild(removeButton);
    optionsContainer.appendChild(entryDiv);

    currentOptionIndex++; // Tăng index cho option mới tiếp theo
}

function removeOptionEntry(button) {
    const entryDiv = button.closest('.option-entry');
    if (entryDiv) {
        entryDiv.remove();
        // Không cần giảm currentOptionIndex ở đây vì nó dùng cho việc thêm mới.
        // Việc xóa và để lại "lỗ hổng" index thường được Spring MVC xử lý.
    }
}

// Các hàm deleteSurvey, deletePost, deleteNotification, deleteUser, verifyStudent giữ nguyên như của bạn

// Gọi hàm khởi tạo khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', initializeQuestionForm);

// Hàm toggleMultipleChoiceOptions cũ của bạn có thể không cần nữa nếu initializeQuestionForm đã bao gồm logic đó
// function toggleMultipleChoiceOptions() { ... } // Xem xét có cần giữ lại hay không











function deleteSurvey(fullUrl) {
    if (confirm("Bạn có chắc chắn muốn xoá khảo sát này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá khảo sát thất bại!");
                });
    }
}


function deletePost(fullUrl) {
    console.log("Delete post URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá bài viết này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá bài viết thất bại!");
                });
    }
}

function deleteNotification(fullUrl) {
    console.log("Delete notification URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá notification này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá thông báo thất bại!");
                });
    }
}


function deleteEvent(fullUrl) {
    console.log("Delete event URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá event này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá event thất bại!");
                });
    }
}
function deleteMember(fullUrl) {
    console.log("Delete member URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá member này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá member thất bại!");
                });
    }
}


function deleteGroup(fullUrl) {
    console.log("Delete group URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá group này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá group thất bại!");
                });
    }
}


function deleteUser(fullUrl) {
    console.log("Delete user URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xoá user này không?")) {
        fetch(fullUrl, {
            method: "DELETE"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xoá user thất bại!");
                });
    }
}




function verifyStudent(fullUrl) {
    console.log("URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn xác thực không?")) {
        fetch(fullUrl, {
            method: "PUT"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("Xác thực thất bại!");
                });
    }
}

function deleteSurveyQuestion(deleteUrl, surveyIdForRedirect) {
    if (confirm("Bạn có chắc chắn muốn xóa câu hỏi này không?")) {
        fetch(deleteUrl, {
            method: "DELETE",
            headers: {
                // Nếu bạn sử dụng Spring Security và CSRF protection, bạn cần thêm CSRF token:
                // 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content'),
                // (Đảm bảo bạn có thẻ meta CSRF trong HTML)
            }
        })
                .then(response => {
                    if (response.ok) {
                        return response.text(); // Mong đợi phản hồi dạng text đơn giản (ví dụ: "success")
                    }
                    // Nếu server trả về lỗi (ví dụ 404, 500), cố gắng đọc text lỗi
                    return response.text().then(text => {
                        throw new Error(text || "Lỗi không xác định từ server. Status: " + response.status);
                    });
                })
                .then(textData => {
                    // Xử lý dựa trên nội dung text trả về từ controller
                    if (textData.toLowerCase().includes("success")) {
                        alert("Xóa câu hỏi thành công!"); // Thông báo tùy chọn
                        // Chuyển hướng về trang danh sách câu hỏi của survey đó
                        // Đảm bảo đường dẫn context path (/SpringSocialApp) là đúng
                        window.location.href = `/SpringSocialApp/questions/${surveyIdForRedirect}`;
                    } else {
                        // textData có thể là "error_not_found" hoặc "error_deleting"
                        alert("Xóa câu hỏi thất bại: " + textData);
                    }
                })
                .catch(error => {
                    console.error('Lỗi khi xóa câu hỏi:', error);
                    alert("Xóa câu hỏi thất bại! " + error.message);
                });
    }
    return false; // Ngăn chặn hành vi mặc định của thẻ <a> nếu dùng với href="#"
}

/**
 * Lọc bảng thống kê chu kỳ dựa trên loại chu kỳ được chọn.
 * @param {string} periodTypeToShow - Loại chu kỳ để hiển thị ('all', 'monthly', 'quarterly', 'yearly').
 * @param {HTMLElement} clickedButton - Nút đã được nhấp (để cập nhật class 'active').
 */
function filterPeriodicStats(periodTypeToShow, clickedButton) {
    const table = document.getElementById('periodicStatsTable');
    if (!table)
        return; // Không làm gì nếu không có bảng

    const tbody = table.getElementsByTagName('tbody')[0];
    const rows = tbody.getElementsByTagName('tr');
    const noStatsRow = document.getElementById('noPeriodicStatsRow'); // Dòng thông báo khi không có dữ liệu sau lọc
    const initialNoStatsRow = tbody.querySelector('tr[th\\:if*="isEmpty(periodicStats)"]'); // Dòng thông báo ban đầu
    let visibleRowCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        // Bỏ qua dòng thông báo "không có dữ liệu" đặc biệt
        if (row.id === 'noPeriodicStatsRow' || (initialNoStatsRow && row === initialNoStatsRow)) {
            continue;
        }

        const rowPeriodType = row.getAttribute('data-period-type');
        if (periodTypeToShow === 'all' || rowPeriodType === periodTypeToShow) {
            row.style.display = ''; // Hiện dòng
            visibleRowCount++;
        } else {
            row.style.display = 'none'; // Ẩn dòng
        }
    }

    // Cập nhật trạng thái active cho các nút
    const buttons = document.querySelectorAll('.btn-group[aria-label="Lọc thống kê chu kỳ"] .btn');
    buttons.forEach(button => {
        button.classList.remove('active', 'btn-primary');
        button.classList.add('btn-secondary');
    });
    if (clickedButton) {
        clickedButton.classList.add('active', 'btn-primary');
        clickedButton.classList.remove('btn-secondary');
    }


    // Hiện/ẩn dòng "Không có thống kê" tùy chỉnh
    if (noStatsRow) {
        if (visibleRowCount === 0 && !(initialNoStatsRow && initialNoStatsRow.style.display !== 'none')) {
            let message = "Không có dữ liệu phù hợp với bộ lọc.";
            if (periodTypeToShow === 'monthly')
                message = "Không có thống kê theo tháng nào.";
            else if (periodTypeToShow === 'quarterly')
                message = "Không có thống kê theo quý nào.";
            else if (periodTypeToShow === 'yearly')
                message = "Không có thống kê theo năm nào.";

            noStatsRow.cells[0].textContent = message;
            noStatsRow.style.display = '';
        } else {
            noStatsRow.style.display = 'none';
        }
    }
    // Ẩn dòng thông báo ban đầu nếu có dòng dữ liệu được hiển thị
    if (initialNoStatsRow) {
        if (visibleRowCount > 0) {
            initialNoStatsRow.style.display = 'none';
        } else if (periodTypeToShow === 'all' && visibleRowCount === 0) { // Nếu lọc all mà vẫn 0 thì hiện lại
            initialNoStatsRow.style.display = '';
        }
    }
}

// Sửa đổi/Thêm vào cuối hàm DOMContentLoaded của bạn
document.addEventListener('DOMContentLoaded', function () {
    initializeQuestionForm(); // Hàm cũ của bạn cho form câu hỏi

    // Thêm logic cho trang thống kê
    if (document.getElementById('periodicStatsTable')) {
        // Tìm nút "Tất cả" và kích hoạt bộ lọc ban đầu
        const allButton = document.querySelector('.btn-group[aria-label="Lọc thống kê chu kỳ"] button');
        if (allButton) {
            filterPeriodicStats('all', allButton);
        }
    }
});