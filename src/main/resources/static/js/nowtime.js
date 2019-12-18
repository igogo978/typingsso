console.log("get now time");
Vue.prototype.$http = axios;
        var app = new Vue({
            el: '#app',
            data: {
                msg: 'hello'
            },
            created: function() {
            }
        })
