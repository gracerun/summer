import http from "@src/apis/http";
import base from "@src/apis/base";

// ${entityDesc}路由
const _${requestMapping}_findByPage = () => { return (params) => { return http.merpost(base.oaIp, "/${requestMapping}/findByPage", params) } }
const _${requestMapping}_findById = () => { return (params) => { return http.merpost(base.oaIp, "/${requestMapping}/findById", params) } }
const _${requestMapping}_create = () => { return (params) => { return http.merpost(base.oaIp, "/${requestMapping}/create", params) } }
const _${requestMapping}_update = () => { return (params) => { return http.merpost(base.oaIp, "/${requestMapping}/update", params) } }
const _${requestMapping}_exportExcel = () => { return (params) => { return http.get(base.oaIp, "/${requestMapping}/exportExcel", params) } }
const _${requestMapping}_exportExcelTmp = () => { return (params) => { return http.get(base.oaIp, "/${requestMapping}/exportExcelTmp", params) } }
export {
_${requestMapping}_findByPage,
_${requestMapping}_findById,
_${requestMapping}_create,
_${requestMapping}_update,
_${requestMapping}_exportExcel,
_${requestMapping}_exportExcelTmp
};