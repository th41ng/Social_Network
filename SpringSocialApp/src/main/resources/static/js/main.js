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
