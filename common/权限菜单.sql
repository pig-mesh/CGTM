-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , #if(!$opensource)默认租户 1#end
#set($menuId=${dateTool.getSystemTime()})

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${menuId}, '-1', '/${moduleName}/${functionName}/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '${tableComment}管理'#if(!$opensource), 1#end);

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${math.add($menuId,1)},${menuId}, '${moduleName}_${functionName}_view', '1', null, '1',  '0', null, '0', null, '${tableComment}查看'#if(!$opensource), 1#end);

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${math.add($menuId,2)},${menuId}, '${moduleName}_${functionName}_add', '1', null, '1',  '0', null, '1', null, '${tableComment}新增'#if(!$opensource), 1#end);

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${math.add($menuId,3)},${menuId}, '${moduleName}_${functionName}_edit', '1', null, '1',  '0', null, '2', null, '${tableComment}修改'#if(!$opensource), 1#end);

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${math.add($menuId,4)},${menuId}, '${moduleName}_${functionName}_del', '1', null, '1',  '0', null, '3', null, '${tableComment}删除'#if(!$opensource), 1#end);

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name#if(!$opensource), tenant_id#end)
values (${math.add($menuId,5)},${menuId}, '${moduleName}_${functionName}_export', '1', null, '1',  '0', null, '3', null, '导入导出'#if(!$opensource), 1#end);