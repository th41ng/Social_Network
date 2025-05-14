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



/**
 * Hàm ẩn/hiện phần nhập lựa chọn trắc nghiệm dựa trên loại câu hỏi được chọn.
 
 */
function toggleMultipleChoiceOptions() {
    const questionTypeSelect = document.getElementById('selectedTypeId');
    const multipleChoiceSection = document.getElementById('multipleChoiceOptionsSection'); 

    if (!questionTypeSelect || !multipleChoiceSection) {
        console.warn("Không tìm thấy các phần tử cần thiết để ẩn/hiện lựa chọn trắc nghiệm.");
        return;
    }

   
    const MULTIPLE_CHOICE_TYPE_ID_VALUE = "1";

    const allOptionInputs = multipleChoiceSection.querySelectorAll('#options input[name="options"]');

    if (questionTypeSelect.value === MULTIPLE_CHOICE_TYPE_ID_VALUE) {
        multipleChoiceSection.style.display = 'block'; // Hiện phần lựa chọn
       
        if (allOptionInputs.length > 0) {
            allOptionInputs[0].required = true;
        }
       
    } else {
        multipleChoiceSection.style.display = 'none'; // Ẩn phần lựa chọn
        // Nếu không phải trắc nghiệm, tất cả các ô lựa chọn không còn bắt buộc nữa.
        allOptionInputs.forEach(input => {
            input.required = false;
        });
    }
}


document.addEventListener('DOMContentLoaded', function () {
   

    const questionTypeSelect = document.getElementById('selectedTypeId');
    if (questionTypeSelect) {
        // Gọi hàm một lần khi tải trang để thiết lập trạng thái ban đầu (quan trọng khi sửa câu hỏi)
        toggleMultipleChoiceOptions();

        // Thêm sự kiện 'change' cho dropdown loại câu hỏi
        questionTypeSelect.addEventListener('change', toggleMultipleChoiceOptions);
    }


});


function addOption() {
    const optionsContainer = document.getElementById("options");

    if (!optionsContainer) {
        console.error("Lỗi: Không tìm thấy vùng chứa các lựa chọn (phần tử với id='options').");
        alert("Đã xảy ra lỗi khi cố gắng thêm lựa chọn. Vui lòng thử làm mới trang.");
        return;
    }

    const newOptionInput = document.createElement('input');
    newOptionInput.type = 'text';
    newOptionInput.className = 'form-control mb-2';
    newOptionInput.name = 'options';
    newOptionInput.placeholder = 'Nhập lựa chọn';


    const questionTypeSelect = document.getElementById('selectedTypeId');
    const MULTIPLE_CHOICE_TYPE_ID_VALUE = "1";
    if (questionTypeSelect && questionTypeSelect.value === MULTIPLE_CHOICE_TYPE_ID_VALUE) {
        newOptionInput.required = true;
    } else {
        newOptionInput.required = false;
    }

    optionsContainer.appendChild(newOptionInput);
}

function verifyStudent(userId) {
    if (confirm("Bạn có chắc chắn muốn xác thực người dùng này?")) {
        fetch(`/api/verify/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            alert(data.message || "Xác thực thành công!");
            location.reload();
        })
        .catch(err => {
            console.error("Xác thực thất bại:", err);
            alert("Đã xảy ra lỗi khi xác thực người dùng!");
        });
    }
}