app.controller('brandController', function ($scope,$controller, brandService) {
    $controller('baseController',{$scope:$scope});//继承
    //查询品牌列表
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    /*  $scope.findPage = function (page, size) {
          $http.post('../brand/findPage.do?page=' + page + '&size=' + size).success(
              function (response) {
                  $scope.list = response.rows;//显示当前页数据
                  $scope.paginationConf.totalItems = response.total;//更新总记录数
              }
          );
      };*/

    //新建与修改
    $scope.save = function () {
        var serviceObject;
        if($scope.entity.id != null){
            serviceObject = brandService.update($scope.entity);
        }else {
            serviceObject = brandService.add($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //如果保存成功则重新加载
                    alert(response.message);
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            });
    };
    //查找单条数据进行修改
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    };

    //批量删除
    $scope.del = function () {
        if (confirm("确定要删除吗")) {
            brandService.del($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();
                    } else {
                        alert(response.message)
                    }
                }
            );
        }
    };

    //条件查询
    $scope.searchEntity={};

    $scope.search = function (page, size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;//显示当前页数据
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


});