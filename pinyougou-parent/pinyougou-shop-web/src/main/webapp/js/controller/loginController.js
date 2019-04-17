app.controller('loginController',function ($scope, $controller, loginService) {
    /*$controller('baseController',{$scope:$scope});*/

    $scope.showLoginName=function () {
        loginService.loginName().success(
            function (response) {
            $scope.loginName=response.loginName;
        });
    }
})