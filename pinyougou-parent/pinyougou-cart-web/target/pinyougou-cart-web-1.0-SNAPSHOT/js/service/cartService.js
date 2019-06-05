app.service('cartService',function ($http) {

    //查找购物车列表
   this.findCartList=function () {
       return $http.get('cart/findCartList.do');
   }

   //增加商品到购物车
    this.addGoodsToCartList=function (itemId, num) {
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    //合计数
    this.sum=function (cartList) {
       //定义合计实体
        var totalValue={totalNum:0,totalMoney:0.00};
        //对购物车列表进行遍历
        for (var i=0;i<cartList.length;i++){
            //拿取商家购物车对象
            var cart=cartList[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                //购物车明细
                var orderItem=cart.orderItemList[j];
                //总数量
                totalValue.totalNum+=orderItem.num;
                //总价格
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        //返回合计数
        return totalValue;
    };

   //查找收获地址
    this.findListByLoginUser=function () {
        return $http.get('address/findListByLoginUser.do');
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }
});