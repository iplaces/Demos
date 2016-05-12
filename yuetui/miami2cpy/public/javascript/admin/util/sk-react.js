export function ajaxJsonPost(url, postData, successFunc) {

    var sendData = JSON.stringify(postData);
    $.ajax({
        url: url,
        dataType: 'json',
        contentType: 'application/json',
        type: 'POST',
        data: sendData,
        success: function (data) {
            var errcode = data.errCode;
            var errmsg = data.msg;
            if (errcode != 0) {
              console.log('errcode=' + errcode + ', errmsg=' + errmsg);
                toastr.error('错误: ' + errmsg);
            } else {
              successFunc(data)
            }
        }.bind(this),
        error: function (xhr, status, err) {
            console.error(this.props.url, xhr, status, err.toString());
            toastr.error('Error:' + err);
        }.bind(this)
    });
}

export function ajaxSimpleGet(url, successFunc) {
    $.ajax({
        url: url,
        dataType: 'json',
        type: 'GET',
        success: function (data) {
            var errcode = data.errCode;
            var errmsg = data.msg;
            if (errcode != 0) {
                console.log('errCode=' + errcode + ', errMsg=' + errmsg);
                toastr.error('错误: ' + errmsg);
            } else {
                successFunc(data)
            }
        }.bind(this),
        error: function (xhr, status, err) {
            console.log("error");
            console.error(url, xhr, status, err.toString());
        }.bind(this)
    });
}