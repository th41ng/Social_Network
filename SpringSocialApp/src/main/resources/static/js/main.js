// Chỉ số cho lựa chọn câu hỏi tiếp theo.
let currentOptionIndex = 0;

// Khởi tạo form câu hỏi, ẩn/hiện lựa chọn trắc nghiệm.
function initializeQuestionForm() {
    const optionsContainer = document.getElementById('optionsContainer');
    if (optionsContainer) {
        currentOptionIndex = optionsContainer.querySelectorAll('.option-entry').length;
    } else {
        currentOptionIndex = 0;
    }

    const questionTypeSelect = document.getElementById('selectedTypeId');
    const multipleChoiceSection = document.getElementById('multipleChoiceOptionsSection');

    // Ẩn/hiện phần lựa chọn tùy theo loại câu hỏi.
    function toggleOptionsVisibility() {
        if (!questionTypeSelect || !multipleChoiceSection) {
            console.warn("toggleOptionsVisibility: Không tìm thấy các phần tử cần thiết (selectedTypeId hoặc multipleChoiceOptionsSection).");
            return;
        }

        const currentSelectedValue = questionTypeSelect.value;
        let isMultipleChoiceType = false;

        console.log("Giá trị (ID) của loại câu hỏi được chọn:", currentSelectedValue);


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

            const firstOptionInput = optionsContainer.querySelector('input[name^="surveyOptions"][name$=".optionText"]');
            if (firstOptionInput && optionsContainer.querySelectorAll('.option-entry').length > 0) {

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

    }
}

// Thêm một lựa chọn mới vào form.
function addOption() {
    const optionsContainer = document.getElementById("optionsContainer");

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

    textInput.name = `surveyOptions[${currentOptionIndex}].optionText`;
    textInput.placeholder = 'Nhập lựa chọn mới';


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
            textInput.required = true;
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

    currentOptionIndex++;
}

// Xóa một lựa chọn khỏi form.
function removeOptionEntry(button) {
    const entryDiv = button.closest('.option-entry');
    if (entryDiv) {
        entryDiv.remove();

    }
}



// Khởi tạo form câu hỏi khi trang tải xong.
document.addEventListener('DOMContentLoaded', initializeQuestionForm);






// --- Các hàm xóa chung ---


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


// --- Các hàm cập nhật trạng thái ---
function banUser(fullUrl) {
    console.log("ban user URL:", fullUrl);
    if (confirm("Bạn có chắc chắn muốn cấm user này không?")) {
        fetch(fullUrl, {
            method: "PUT"
        })
                .then(res => {
                    if (res.ok)
                        location.reload();
                    else
                        alert("ban user thất bại!");
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


// Xóa câu hỏi khảo sát và chuyển hướng.
function deleteSurveyQuestion(deleteUrl, surveyIdForRedirect) {
    if (confirm("Bạn có chắc chắn muốn xóa câu hỏi này không?")) {
        fetch(deleteUrl, {
            method: "DELETE",
            headers: {

            }
        })
                .then(response => {
                    if (response.ok) {
                        return response.text();
                    }

                    return response.text().then(text => {
                        throw new Error(text || "Lỗi không xác định từ server. Status: " + response.status);
                    });
                })
                .then(textData => {

                    if (textData.toLowerCase().includes("success")) {
                        alert("Xóa câu hỏi thành công!");

                        window.location.href = `/SpringSocialApp/questions/${surveyIdForRedirect}`;
                    } else {

                        alert("Xóa câu hỏi thất bại: " + textData);
                    }
                })
                .catch(error => {
                    console.error('Lỗi khi xóa câu hỏi:', error);
                    alert("Xóa câu hỏi thất bại! " + error.message);
                });
    }
    return false;
}

// Lọc bảng thống kê chu kỳ
function filterPeriodicStats(periodTypeToShow, clickedButton) {
    const table = document.getElementById('periodicStatsTable');
    if (!table)
        return;

    const tbody = table.getElementsByTagName('tbody')[0];
    const rows = tbody.getElementsByTagName('tr');
    const noStatsRow = document.getElementById('noPeriodicStatsRow');
    const initialNoStatsRow = tbody.querySelector('tr[th\\:if*="isEmpty(periodicStats)"]');
    let visibleRowCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];

        if (row.id === 'noPeriodicStatsRow' || (initialNoStatsRow && row === initialNoStatsRow)) {
            continue;
        }

        const rowPeriodType = row.getAttribute('data-period-type');
        if (periodTypeToShow === 'all' || rowPeriodType === periodTypeToShow) {
            row.style.display = '';
            visibleRowCount++;
        } else {
            row.style.display = 'none';
        }
    }


    const buttons = document.querySelectorAll('.btn-group[aria-label="Lọc thống kê chu kỳ"] .btn');
    buttons.forEach(button => {
        button.classList.remove('active', 'btn-primary');
        button.classList.add('btn-secondary');
    });
    if (clickedButton) {
        clickedButton.classList.add('active', 'btn-primary');
        clickedButton.classList.remove('btn-secondary');
    }



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

    if (initialNoStatsRow) {
        if (visibleRowCount > 0) {
            initialNoStatsRow.style.display = 'none';
        } else if (periodTypeToShow === 'all' && visibleRowCount === 0) { // Nếu lọc all mà vẫn 0 thì hiện lại
            initialNoStatsRow.style.display = '';
        }
    }
}

// Khởi tạo các chức năng khi trang tải xong
document.addEventListener('DOMContentLoaded', function () {
    initializeQuestionForm();


    if (document.getElementById('periodicStatsTable')) {

        const allButton = document.querySelector('.btn-group[aria-label="Lọc thống kê chu kỳ"] button');
        if (allButton) {
            filterPeriodicStats('all', allButton);
        }
    }
});