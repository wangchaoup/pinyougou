//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        //获取页面传来的id值,
        var id = $location.search()['id'];
        //没有id直接返回，不做处理
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //富文本编辑器内容回显
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表,图片以文本格式存储，因此需要进行转换以便在页面回显
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //显示扩展属性,与增加商品的监控代码冲突，需要增加判断条件
                $scope.entity.goodsDesc.customAttributeItems =
                    JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //显示规格,需要对规格选项进行处理
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //SKU列表显示，对类目进行遍历，json转换
                for(var i=0;i<$scope.entity.itemList.length;i++){
                    //每条都需要转换
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        );
    }

    //保存
    $scope.save = function () {
        //提取文本编辑器的内容
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        //商品id
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //保存成功后返回信息，清空内容
                    alert("修改成功");
                    //修改成功直接跳转
                    location.href='goods.html';
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //添加,不需要进行刷新
    $scope.add = function () {
        //富文本编辑器，拿到内容并传给后台
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert("保存成功");
                    //清空已保存的内容
                    $scope.entity = {};
                    //清空富文本编辑器
                    editor.html("");
                } else {
                    alert(response.message);
                }
            }
        );

    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //图片上传
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    //上传成功拿到url
                    $scope.image_entity.url = response.message;
                } else {
                    alert("上传失败");
                }
            }).error(
            function () {
                alert("发生错误，上传失败")
            }
        );
    }

    //定义组合实体在页面的实体
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}}
    //添加图片到实体类中,然在页面拿到图片列表的数据
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //移除列表中的图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //一级目录,根据一级目录进行查询，在三级目录下添加商品详细信息
    $scope.selectItemCatList = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        );
    }

    //二级目录

    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            });
    })

    //三级目录
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            });
    })

    //获取三级选项模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    })

    //品牌下拉框,监测的是将为本方法提供数据的对象
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                //定义模板类型
                $scope.typeTemplate = response;
                //获取品牌列表,把自身转换为json对象
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //获取扩展属性  属性回显时会发生冲突，因此需要进行判断，没有id则进行添加操作
                //否则就可以进行属性回显
                if ($location.search()['id'] == null) {

                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
                }
            }
        );
        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );

    })

    //定义商品的格式，对图片和规格选项进行保存
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecAttribute = function ($event, name, value) {

        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);

        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {//取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);//移除选项
                //如果选项都取消了，将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }

            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }

    }

    //创建SKU表
    $scope.createItemList = function () {
        //初始化列表
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//列表初始化

        var items = $scope.entity.goodsDesc.specificationItems;
        //遍历添加
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }

    }
    //克隆添加列表
    addColumn = function (list, columnName, columnValues) {

        var newList = [];
        for (var i = 0; i < list.length; i++) {
            //基础表
            var oldRow = list[i];
            //动态添加扩展字段表
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    //显示商品状态，定义状态数组来存储状态，状态码将作为数组的角标，由此来实现页面显示状态中文信息
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    //把商品分类条目封装为数组，根据id取值，拿到分类的条目
    //定义数组
    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (i = 0; i < response.length; i++) {
                    //把分类封装为数组
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }

    //判断规格名称和选项名称是否被勾选
    $scope.checkAttributeValue=function (specName, optionName) {
        //定义商品规格详细条目
        var items= $scope.entity.goodsDesc.specificationItems;
        //通过键来查询商品规格回显对象（网络、规格等作为键）
        var object= $scope.searchObjectByKey(items,'attributeName',specName);
        if(object == null){
            return false;
        }else{
            //若存在值，则根据规格名筛选规格，判断返回值，通过ng-checked来确定是否勾选
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }

});
