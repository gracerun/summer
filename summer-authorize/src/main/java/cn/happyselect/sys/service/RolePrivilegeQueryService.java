package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.sys.bean.dto.RoleUrlQueryDto;
import cn.happyselect.sys.bean.vo.RoleUrlVo;
import cn.happyselect.sys.mapper.RolePrivilegeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 角色权限查询业务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-10
 */
@Service
@Slf4j
public class RolePrivilegeQueryService {

    @Autowired
    private RolePrivilegeMapper rolePrivilegeMapper;

    public Map<String, List<String>> findAllRoleUrlPrivilege() {
        RoleUrlQueryDto roleUrlQueryDto = new RoleUrlQueryDto();
        roleUrlQueryDto.setStatus(StatusConstant.TRUE);
        List<RoleUrlVo> urlPrivileges = rolePrivilegeMapper.selectAllPrivilegeUrl(roleUrlQueryDto);

        Map<String, List<String>> urlMap = new HashMap<>(128);
        for (RoleUrlVo roleUrlVo : urlPrivileges) {
            if (!StringUtils.hasText(roleUrlVo.getUrl())
                    || !StringUtils.hasText(roleUrlVo.getRoleCode())) {
                log.error("{} url or roleCode is empty ", roleUrlVo);
                continue;
            }

            List<String> roleCodes = urlMap.get(roleUrlVo.getUrl());
            if (Objects.isNull(roleCodes)) {
                roleCodes = new ArrayList<>();
                urlMap.put(roleUrlVo.getUrl(), roleCodes);
            }
            roleCodes.add(roleUrlVo.getRoleCode());
            urlMap.put(roleUrlVo.getUrl(), roleCodes);
        }

        return urlMap;
    }
}
