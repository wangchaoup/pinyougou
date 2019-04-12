//品牌控制层
app.controller('baseController',function ($scope) {
    //刷新列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };

    //复选框
    $scope.selectIds = []; //初始化
    //复选框处理
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            //添加元素
            $scope.selectIds.push(id);
        } else {
            //未选中则剔除
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1); // 位置  个数
        }
    };
});