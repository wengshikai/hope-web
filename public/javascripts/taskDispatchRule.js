;(function ($) {
    //切换普通模式和自由分配
    $('#simpleModeRadio').on('click',function () {
        $('#simpleMode').css('display', 'block');
        $('#freeMode').css('display', 'none');
    }).trigger('click');

    $('#freeModeRadio').on('click',function () {
        $('#simpleMode').css('display', 'none');
        $('#freeMode').css('display', 'block');
    });


    //小组任务输入框获得焦点时触发
    $('.tableTaskNumByTeam').on('focus',function() {
        //先清空自动修改的属性
        $(this).parent('td').siblings('.tableErrorMessage').text('');
        $(this).parent('td').siblings('.tableTaskNumByBuyer').text('');
        //把按钮锁住
        $('#freeModeButton').attr('disabled','');
        $('#freeModeButton').removeClass('btn-info');
        $('#freeModeButton').addClass('btn-disabled');
        //将剩余订单数置空
        $('#notDispatchedCount').text('');
        $('#notDispatchedCount').css('color','');
    });


    //小组任务输入框失去焦点时触发
    $('.tableTaskNumByTeam').on('blur',function() {
        let taskNum = $(this).val().trim();
        let isNum = /^[1-9][0-9]*$/;
        //如果没有输入，不处理此单元格内容
        if (taskNum !== '') {
            //判断填入的值是否合法
            if (!isNum.test(taskNum)) {
                $(this).parent('td').siblings('.tableErrorMessage').text('请输入正整数');
                return;
            }

            taskNum = Number(taskNum); //强制转换成数字
            //计算平均每个刷手的订单
            let buyerCount = Number($(this).parent('td').siblings('.tableBuyerCount').eq(0).text().trim());
            let taskCountByBuyer = taskNum / buyerCount;

            if (isNum.test(taskCountByBuyer.toString())) {
                //判断如果刷手平均单数是个整数，那么直接输出数字
                $(this).parent('td').siblings('.tableTaskNumByBuyer').text(taskCountByBuyer);
            } else {
                //否则，输出上下取整的数字
                $(this).parent('td').siblings('.tableTaskNumByBuyer').text(`${Math.floor(taskCountByBuyer)}或${Math.ceil(taskCountByBuyer)}`);
            }

            //判断是否超过20单,如果是,那么跳出提示
            if (taskCountByBuyer > 20) {
                $(this).parent('td').siblings('.tableErrorMessage').text('每个刷手最多分配20单');
            }
        }

        //计算总剩余单数
        let allDispatchedTaskNum = 0;
        $('#freeMode').find('.tableTaskNumByTeam').each(function (){
            if(isNum.test($(this).val().trim())) {
                allDispatchedTaskNum += Number($(this).val().trim());
            }
        });
        let allTaskNum = Number($('#allTaskNum').text().trim());
        let notDisPatchedTaskNum = allTaskNum - allDispatchedTaskNum;
        //填写剩余单数
        $('#notDispatchedCount').text(notDisPatchedTaskNum);
        if (notDisPatchedTaskNum < 0) {
            //如果剩余单数少于0,标红
            $('#notDispatchedCount').css('color','#F00')
        } else if (notDisPatchedTaskNum > 0) {
            //如果剩余单数大于0,标蓝
            $('#notDispatchedCount').css('color','#00F')
        } else if  (notDisPatchedTaskNum === 0) {
            //如果剩余单数等于0,标绿
            $('#notDispatchedCount').css('color','#0F0')
        }

        //先判断有没有错误信息,如果有,那么不解锁按钮
        $('#freeMode').find('.tableErrorMessage').each(function () {
            if ($(this).text().trim() !=='') {
                return;
            }
        });

        //如果总订单数不为0,且剩余订单数为0,那么解锁按钮
        if (allTaskNum !== 0 && notDisPatchedTaskNum === 0) {
            $('#freeModeButton').removeAttr('disabled');
            $('#freeModeButton').removeClass('btn-disabled');
            $('#freeModeButton').addClass('btn-info');
        }
    });

})(jQuery);