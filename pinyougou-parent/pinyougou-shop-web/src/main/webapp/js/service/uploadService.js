//文件上传服务层,请求部分固定
app.service("uploadService", function ($http) {
    this.uploadFile = function () {
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http({
            method: 'POST',
            url: "../upload.do",
            data: formData,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        });
    }
});
//anjularjs 对于 post 和 get 请求默认的 Content-Type header 是 application/json。通过设置
//‘Content-Type’: undefined，这样浏览器会把 Content-Type 设置为 multipart/form-data.
//通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化 formdata object
