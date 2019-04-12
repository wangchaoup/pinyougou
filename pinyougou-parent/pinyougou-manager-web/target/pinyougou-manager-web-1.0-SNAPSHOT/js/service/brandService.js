app.service("brandService",function ($http) {
            //查询所有
    this.findAll=function () {
       return $http.get("../brand/findAll.do");
    }

    //新建
    this.add=function (entity) {
        return $http.post('../brand/add.do', entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../brand/update.do', entity);
    }
    //查找单条数据
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }
    //批量删除
    this.del=function (selectIds) {
        return $http.get('../brand/del.do?ids=' +selectIds);
    }
    //条件查询及查询所有
    this.search=function (page,size,searchEntity) {
        return $http.post('../brand/findPage.do?page=' + page + '&size=' + size,searchEntity)
    }
    //下拉列表
    this.selectOptionList=function(){
    return $http.post('../brand/selectOptionList.do')
    }
});