app.controller('payController',function ($scope,payService) {
    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                //$scope.code_url=response.code_url;
                $scope.total_fee=response.total_fee;
                $scope.out_trade_no=response.out_trade_no;
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
            }
        );
    }
});