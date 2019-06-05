app.controller('searchController',function ($scope,$location, searchService) {

    $scope.search=function () {
        //把页码转换为int类型，否则可能出现提交的数据为String类型，造成调用失败
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                //返回搜索结果
                $scope.resultMap=response;
                //调用分页
                buildPageLable();
            }
        );
    }

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':30,'sortField':'','sort':''};//搜索对象封装
    //添加搜索项
    $scope.addSearchItem=function (key, value) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            //如果点击的是分类或者品牌
            $scope.searchMap[key]=value;
        }else{
            //是否是规格
            $scope.searchMap.spec[key]=value;
        }
        //添加符合搜索条件,执行搜索
        $scope.search();
    }

    //撤销搜索
    $scope.removeSearchItem=function (key) {
        //如果是分类或者品牌
        if(key =="category" || key =="brand" || key == 'price'){
            $scope.searchMap[key]="";
        }else{
            //否则是规格,移除此属性
            delete $scope.searchMap.spec[key];
        }
        //隐藏或移除后都要进行重新查询
        $scope.search();
    }


    //分页
    buildPageLable=function () {
        //新增分页栏属性
        $scope.pageLable=[];
        //定义最大分页数
        var maxPageNo = $scope.resultMap.totalPages;
        //起始页码
        var firstPage=1;
        //截至页码
        var lastPage=maxPageNo;
        //页码前边有省略号
        $scope.firstDot=true;
        //页码后边有省略号
        $scope.lastDot=true;
        //总页数大于5显示部分页码
        if ($scope.resultMap.totalPages > 5){
           //当前页小于等于三
            if ($scope.searchMap.pageNo<3){
                lastPage=5;
                //前边不需要有点
                $scope.firstDot=false;
            }else if($scope.searchMap.pageNo>=lastPage-2){//如果当前页比最大页还大，则最后一页为最大页
                //起始页
                firstPage=maxPageNo-4;
                //后边不需要有点
                $scope.lastDot=false;
            }else {
                //正常显示
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            //总页数小于五的需要有省略号
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        //循环产生页码标签
        for (var i=firstPage;i<=lastPage;i++){
            $scope.pageLable.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        //页码验证
        if(pageNo <1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        //是否为当前页
        $scope.searchMap.pageNo=pageNo;
        //调用方法查询
        $scope.search();
    }

    //判断当前页是否为第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    }

    //判断当前页是否为最后一页
    $scope.isEndPage=function () {
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }

    };

    //设置排序规则
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    };


    //判断关键字里是否包含品牌
    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.searchMap.brandList.length;i++){
            //搜索框中的关键字是否包含品牌
            if ($scope.searchMap.keywords.indexOf($scope.searchMap.brandList[i].text)>=0){
                return true;
            }else{
                return false;
            }
        }
    }

    //接收搜索的关键字并跳转
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        //查询
        $scope.search();
    }
});