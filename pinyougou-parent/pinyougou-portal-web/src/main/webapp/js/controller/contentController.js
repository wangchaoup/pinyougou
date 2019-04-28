//控制层
app.controller('contentController', function ($scope, $controller, contentService) {


    //定义广告集合，多类广告同时查询
    $scope.contentList = [];
    //查询分类下的可用广告内容
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (resopnse) {
                $scope.contentList[categoryId] = resopnse;
            }
        );
    }

    //搜索跳转
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});	
