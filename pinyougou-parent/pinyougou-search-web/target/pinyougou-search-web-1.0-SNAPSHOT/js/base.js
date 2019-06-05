var app=angular.module('pinyougou',[]);

// $sce服务，过滤器,信任策略
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);