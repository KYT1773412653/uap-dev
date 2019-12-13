package com.hexing.uap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hexing.uap.bean.custom.OrgTreeNode;
import com.hexing.uap.bean.jpa.UapOrganization;
import com.hexing.uap.bean.jpa.UapUser;
import com.hexing.uap.bean.jpa.UapUserOrgManage;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.message.OrgResponseCode;
import com.hexing.uap.repository.OrganizationRepository;
import com.hexing.uap.repository.UserOrgManageRepository;
import com.hexing.uap.repository.UserRepository;
import com.hexing.uap.repository.custom.OrgCustomRepository;
import com.hexing.uap.repository.custom.UserOrgManageCustomRepository;
import com.hexing.uap.repository.param.OrgQuery;
import com.hexing.uap.repository.param.UnassignedOrgQuery;
import com.hexing.uap.repository.param.UserOrgManageQuery;
import com.hexing.uap.util.ConvertUtil;
import com.hexing.uap.util.DateTimeUtil;
import com.hexing.uap.util.MathUtil;

/**
 * <p>
 * 
 * @author hua.zhiwei<br>
 */
@Service
@Transactional
public class UserOrgManageService {

	@Autowired
	private UserOrgManageRepository userOrgManageRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserOrgManageCustomRepository userOrgManageCustomRepository;

	@Autowired
	private OrgCustomRepository orgCustomRepository;

	/**
	 * Des:批量删除管理单位
	 * @author hua.zhiwei<br>
	 * @param idList
	 * @return <br>
	 */
	public String remove(List<Long> idList) {
		for (Long id : idList) {
			userOrgManageRepository.deleteById(id);
		}
		return OrgResponseCode.OPERATE_SUCCESS;

	}

		/**
	 * Des:删除用户的所有管理单位
	 * @author hua.zhiwei<br>
	 * @param userId
	 * @return <br>
	 */
	public String removeByUser(Long userId) {
		userOrgManageCustomRepository.deleteByUserId(userId);
		return OrgResponseCode.OPERATE_SUCCESS;

	}

	/**
	 * Des:查找分配给指定用户的所有管理单位信息
	 * @author hua.zhiwei<br>
	 * @param query
	 * @return <br>
	 */
	public PageData<UapUserOrgManage> findByUser(UserOrgManageQuery query) {
		return userOrgManageCustomRepository.findSubOrgs(query);
	}

	public List<UapUserOrgManage> findByOrg(Long orgId) {
		return userOrgManageRepository.findByOrgId(orgId);
	}
	
	/**
	 * Des:查找未分配给指定用户的所有子组织
	 * @author hua.zhiwei<br>
	 * @param query
	 * @return <br>
	 */
	public PageData<UapOrganization> findPageData(UnassignedOrgQuery query) {
		PageData<UapOrganization> resultList = new PageData<UapOrganization>();
		Optional<UapOrganization> parentOrg = organizationRepository.findById(query.getOrgId());
		if (!parentOrg.isPresent()) {
			return resultList;
		}
		String pathId = parentOrg.get().getOrgPathId();
		List<Long> orgIds = userOrgManageCustomRepository.queryOrgIdByUserId(query.getUserId());
		query.setOrgIds(orgIds);
		query.setPathId(pathId);
		query.setOrgNo(parentOrg.get().getNo());
		resultList = orgCustomRepository.findSubOrgs(query);
		return resultList;
	}

	/**
	 * Des:查找指定用户所属组织的所有下级组织，以树的形式返回
	 * @author hua.zhiwei<br>
	 * @param userId
	 * @return <br>
	 */
	@Deprecated
	public OrgTreeNode findAllSubOrgs(Long userId) {
//		StopWatch watch = new StopWatch();
//		watch.start();
//		System.out.println("findAllSubOrgs begin ");
		UapUser user = userService.findById(userId);
		if (null == user) {
			return null;
		}
		UapOrganization org = user.getUapOrganization();
		String pathId = org.getOrgPathId();
		if (StringUtils.isEmpty(pathId)) {
			return null;
		}
		List<UapOrganization> allSubOrgs = findAllSubOrg(org, user.getUapMultiTenancy().getId());
//		watch.stop();
//		System.out.println("findAllSubOrg END, org size  " + allSubOrgs.size());
//		System.out.println("findAllSubOrg cost time " + watch.getTotalTimeMillis());
//		watch.start();
		Long orgId = org.getId();
		OrgTreeNode node = getOrgTreeNode(org);
		List<Long> orgIds = userOrgManageCustomRepository.queryOrgIdByUserId(userId);
		if (orgIds.contains(orgId)) {
			node.setChecked(true);
		}
		getOrgTree(allSubOrgs, orgId, node, orgIds);
//		watch.stop();
//		System.out.println("getOrgTree end, total cost time " + watch.getTotalTimeMillis());
		return node;

	}

	/**
	 * Des:给用户批量添加管理单位
	 * @author hua.zhiwei<br>
	 * @param user 
	 * @param orgs
	 * @return <br>
	 */
	public String addOrgForUser(UapUser user, List<UapOrganization> orgs) {
		//userOrgManageCustomRepository.deleteByUserId(user.getId());
		List<Long> orgIds = userOrgManageCustomRepository.queryOrgIdByUserId(user.getId());
		List<UapUserOrgManage> batchArgs = new ArrayList<>();
		Long maxId = userOrgManageCustomRepository.queryMaxId();
		long id = MathUtil.getNonNull(maxId);
		long now = DateTimeUtil.clock.millis();
		
		for (UapOrganization org : orgs) {
			UapUserOrgManage manage = new UapUserOrgManage();
			if (orgIds.contains(org.getId())) {
				continue;
			}
			id += 1;
			manage.setId(id);
			manage.setIsCascade(UapConstant.ORG_MANAGE_NOT_CASCADE);
			manage.setOrgId(org.getId());
			manage.setOrgName(org.getName());
			manage.setOrgNo(org.getNo());
			manage.setOrgPathId(org.getOrgPathId());
			manage.setUpTime(now);
			manage.setUserId(user.getId());
			manage.setUserName(user.getName());
			manage.setUserNo(user.getNo());
			batchArgs.add(manage);
//			batchArgs.add(new Object[] { id, user.getNo(), user.getId(), user.getName(), org.getId(),
//					org.getName(), org.getNo(), org.getOrgPathId(), UapConstant.ORG_MANAGE_NOT_CASCADE, now});
		}
		userOrgManageCustomRepository.batchInsert(batchArgs);
		return OrgResponseCode.OPERATE_SUCCESS;

	}
	
	
	/**
	 * <p>
	 * 给用户添加可管理组织.此方法过时，改用addOrgForUser
	 * 
	 * @author hua.zhiwei<br>
	 * @param uapUserOrgManage 用户信息
	 * @param orgs             可管理组织ID集合
	 * @return 处理结果<br>
	 */
    @Deprecated
	public String addForUser(UapUserOrgManage uapUserOrgManage, List<Long> orgIds) {
		UapUser user = userService.findById(uapUserOrgManage.getUserId());
		user = userService.findByNo(user.getNo());
		uapUserOrgManage.setUserName(user.getName());
		uapUserOrgManage.setUserNo(user.getNo());
		UapOrganization uapOrganization = user.getUapOrganization();
		// 判断传入的组织是否和用户所属组织相同或为其下属组织，用户只能管理自己所在的组织和下级组织
		List<UapOrganization> orgList = organizationRepository.findByIdIn(orgIds);
		for (UapOrganization uapOrg : orgList) {
			if (!organizationService.checkSubOrSame(uapOrganization, uapOrg)) {
				return OrgResponseCode.NOT_SUB_ORG_OR_SAME;
			}
		}
		// 查出该用户原有的可管理组织信息
		List<UapUserOrgManage> userOrgList = userOrgManageRepository.findByUserId(uapUserOrgManage.getUserId());
		List<UapUserOrgManage> userOrgForAddList = Lists.newArrayList();
		List<Long> userOrgForDeleteList = Lists.newArrayList();
		// 如果用户之前没有配置过可管理组织，本次直接新增
		if (CollectionUtils.isEmpty(userOrgList)) {
			for (UapOrganization uapOrg : orgList) {
				addUserOrgManage(uapOrg, uapUserOrgManage, userOrgForAddList);
			}
		} else {
			// 把用户原来的可管理组织信息转到map存储，map的key为组织ID，方便后面获取
			Map<Long, UapUserOrgManage> oriUserOrgMap = Maps.newHashMap();
			// List<Long> oriOrgIds = Lists.newArrayList();
			for (UapUserOrgManage userOrg : userOrgList) {
				// oriOrgIds.add(userOrg.getOrgId());
				oriUserOrgMap.put(userOrg.getOrgId(), userOrg);
			}
			// 查询出用户之前配置的可管理组织列表，用于后面比对级连信息
			// List<UapOrganization> oriOrgList =
			// organizationRepository.findByIdIn(oriOrgIds);
			// 如果本次传过来的组织是级连的，需要保证之前的用户可管理组织中没有本次传过来的组织的下级组织
			// 另外，如果之前的用户可管理组织中有级连组织，且本次传过来的组织有其下级组织，则该组织不需要处理
			if (UapConstant.ORG_MANAGE_CASCADE.equals(uapUserOrgManage.getIsCascade())) {
				for (UapOrganization uapOrg : orgList) {
					// 之前的用户可管理组织是本次传过来的组织的下级组织，则原来配置的组织需要删除
					if (checkSubOrSameOrg(uapOrg, oriUserOrgMap.get(uapOrg.getId()))) {
						userOrgForDeleteList.add(oriUserOrgMap.get(uapOrg.getId()).getId());
						addUserOrgManage(uapOrg, uapUserOrgManage, userOrgForAddList);
						// 如果之前的用户可管理组织中有级连组织，且本次传过来的组织有其下级组织，则该组织不需要处理
					} else if (checkSubOrSameOrg(oriUserOrgMap.get(uapOrg.getId()), uapOrg)
							&& UapConstant.ORG_MANAGE_CASCADE
									.equals(oriUserOrgMap.get(uapOrg.getId()).getIsCascade())) {
						continue;
					} else {
						addUserOrgManage(uapOrg, uapUserOrgManage, userOrgForAddList);
					}
				}
			} else {
				// 如果本次传过来的组织不是级连的，如果之前的用户可管理组织中有级连组织，且本次传过来的组织有其下级组织，则该组织不需要处理
				for (UapOrganization uapOrg : orgList) {
					if (checkSubOrSameOrg(oriUserOrgMap.get(uapOrg.getId()), uapOrg) && UapConstant.ORG_MANAGE_CASCADE
							.equals(oriUserOrgMap.get(uapOrg.getId()).getIsCascade())) {
						continue;
					} else if (oriUserOrgMap.get(uapOrg.getId()) != null) {
						continue;
					} else {
						addUserOrgManage(uapOrg, uapUserOrgManage, userOrgForAddList);
					}
				}
			}
		}
		if (!CollectionUtils.isEmpty(userOrgForDeleteList)) {
			for (Long id : userOrgForDeleteList) {
				userOrgManageRepository.deleteById(id);
			}
		}
		userOrgManageRepository.saveAll(userOrgForAddList);
		return OrgResponseCode.OPERATE_SUCCESS;

	}
    
	/**
	 * <p>
	 * 给组织添加管理用户
	 * 
	 * @author hua.zhiwei<br>
	 * @param uapUserOrgManage 组织
	 * @param users            用户ID集合
	 * @return 处理结果<br>
	 */
    @Deprecated
	public String addForOrg(UapUserOrgManage uapUserOrgManage, List<Long> userIds) {
		UapOrganization uapOrganization = organizationService.get(uapUserOrgManage.getOrgId());
		uapUserOrgManage.setOrgName(uapOrganization.getName());
		uapUserOrgManage.setOrgNo(uapOrganization.getNo());
		List<UapUser> users = userRepository.findByIdIn(userIds);
		// 查出传入的用户原有的可管理组织信息
		List<UapUserOrgManage> userOrgList = userOrgManageRepository.findByUserIdIn(userIds);
		List<UapUserOrgManage> userOrgForAddList = Lists.newArrayList();
		List<Long> userOrgForDeleteList = Lists.newArrayList();
		for (UapUser user : users) {
			
			// 找出用户原有的可管理组织信息
			List<UapUserOrgManage> oriUserOrgList = userOrgList.stream()
					.filter(userOrg -> user.getId().equals(userOrg.getUserId())).collect(Collectors.toList());
			// 如果用户之前没有配置过可管理组织，本次直接新增
			if (CollectionUtils.isEmpty(oriUserOrgList)) {
				addUserOrgManage(user, uapUserOrgManage, userOrgForAddList);
			} else {
				// 把用户原来的可管理组织信息转到map存储，map的key为组织ID，方便后面获取
				Map<Long, UapUserOrgManage> oriUserOrgMap = Maps.newHashMap();
				// List<Long> oriOrgIds = Lists.newArrayList();
				for (UapUserOrgManage oriUserOrg : oriUserOrgList) {
					// oriOrgIds.add(oriUserOrg.getOrgId());
					oriUserOrgMap.put(oriUserOrg.getOrgId(), oriUserOrg);
				}
				// 查询出用户之前配置的可管理组织列表，用于后面比对级连信息
				// List<UapOrganization> oriOrgList =
				// organizationRepository.findByIdIn(oriOrgIds);
				// 如果本次传过来的组织是级连的，需要保证之前的用户可管理组织中没有本次传过来的组织的下级组织
				// 另外，如果之前的用户可管理组织中有级连组织，且本次传过来的组织有其下级组织，则该组织不需要处理
				if (UapConstant.ORG_MANAGE_CASCADE.equals(uapUserOrgManage.getIsCascade())) {
					// 之前的用户可管理组织是本次传过来的组织的下级组织，则原来配置的组织需要删除
					if (checkSubOrSameOrg(uapOrganization, oriUserOrgMap.get(uapOrganization.getId()))) {
						userOrgForDeleteList.add(oriUserOrgMap.get(uapOrganization.getId()).getId());
						addUserOrgManage(user, uapUserOrgManage, userOrgForAddList);
					} else if (checkSubOrSameOrg(oriUserOrgMap.get(uapOrganization.getId()), uapOrganization)
							&& UapConstant.ORG_MANAGE_CASCADE
									.equals(oriUserOrgMap.get(uapOrganization.getId()).getIsCascade())) {
						continue;
					} else {
						addUserOrgManage(user, uapUserOrgManage, userOrgForAddList);
					}
				} else {
					// 如果本次传过来的组织不是级连的，如果之前的用户可管理组织中有级连组织，且本次传过来的组织有其下级组织，则该组织不需要处理
					if (checkSubOrSameOrg(oriUserOrgMap.get(uapOrganization.getId()), uapOrganization)
							&& UapConstant.ORG_MANAGE_CASCADE
									.equals(oriUserOrgMap.get(uapOrganization.getId()).getIsCascade())) {
						continue;
					} else if (oriUserOrgMap.get(uapOrganization.getId()) != null) {
						continue;
					} else {
						addUserOrgManage(user, uapUserOrgManage, userOrgForAddList);
					}
				}
			}
		}
		if (!CollectionUtils.isEmpty(userOrgForDeleteList)) {
			for (Long id : userOrgForDeleteList) {
				userOrgManageRepository.deleteById(id);
			}
		}
		userOrgManageRepository.saveAll(userOrgForAddList);
		return OrgResponseCode.OPERATE_SUCCESS;

	}

	private OrgTreeNode getOrgTreeNode(UapOrganization org) {
		OrgTreeNode node = ConvertUtil.convert(org, OrgTreeNode.class);
		node.setChildren(Lists.newArrayList());
		return node;
	}

	private void getOrgTree(List<UapOrganization> allSubOrgs, Long orgId, OrgTreeNode node, List<Long> orgIds) {
		if (!CollectionUtils.isEmpty(allSubOrgs)) {
			Map<Long, List<UapOrganization>> map = allSubOrgs.stream()
					.filter(org -> !orgId.equals(org.getId()) && null != org.getParentId())
					.collect(Collectors.groupingBy(UapOrganization::getParentId));
			Set<Long> parentIds = map.keySet();
			Map<Long, UapOrganization> parentMap = allSubOrgs.stream()
					.filter(org -> org.getId().equals(orgId) || parentIds.contains(org.getId()))
					.collect(Collectors.toMap(UapOrganization::getId, o -> o));
			Map<Long, OrgTreeNode> nodeMap = Maps.newHashMap();
			nodeMap.put(orgId, node);
			map.forEach((parentId, subOrgs) -> {
				OrgTreeNode parentNode = nodeMap.get(parentId);
				if (null == parentNode) {
					parentNode = getOrgTreeNode(parentMap.get(parentId));
					nodeMap.put(parentId, parentNode);
				}
				for (UapOrganization org : subOrgs) {
					OrgTreeNode subNode = nodeMap.get(org.getId());
					if (null == subNode) {
						subNode = getOrgTreeNode(org);
						nodeMap.put(org.getId(), subNode);
					}
					if (orgIds.contains(org.getId())) {
						subNode.setChecked(true);
					}
					if (subNode.isChecked()) {
						parentNode.setChecked(true);
					}
					parentNode.getChildren().add(subNode);
					//parentNode.addChild(subNode);
				}
			});
		}
	}

	private boolean checkSubOrSameOrg(UapUserOrgManage parent, UapOrganization child) {
		if (null == parent || null == child) {
			return false;
		} else if (parent.getOrgId().equals(child.getId()) || parent.getOrgId().equals(child.getParentId())) {
			return true;
		} else if (StringUtils.isEmpty(parent.getOrgNo()) || StringUtils.isEmpty(child.getNo())) {
			return false;
		} else {
			return child.getNo().startsWith(parent.getOrgNo());
		}
	}

	private boolean checkSubOrSameOrg(UapOrganization parent, UapUserOrgManage child) {
		if (null == parent || null == child) {
			return false;
		} else if (parent.getId().equals(child.getOrgId())) {
			return true;
		} else if (StringUtils.isEmpty(parent.getNo()) || StringUtils.isEmpty(child.getOrgNo())) {
			return false;
		} else {
			return child.getOrgNo().startsWith(parent.getNo());
		}
	}

	private void addUserOrgManage(UapOrganization uapOrg, UapUserOrgManage uapUserOrgManage,
			List<UapUserOrgManage> userOrgList) {
		UapUserOrgManage uapUserOrg = new UapUserOrgManage();
		uapUserOrg.setIsCascade(uapUserOrgManage.getIsCascade());
		uapUserOrg.setOrgId(uapOrg.getId());
		uapUserOrg.setOrgName(uapOrg.getName());
		uapUserOrg.setUpTime(DateTimeUtil.clock.millis());
		uapUserOrg.setUserId(uapUserOrgManage.getUserId());
		uapUserOrg.setUserName(uapUserOrgManage.getUserName());
		uapUserOrg.setUserNo(uapUserOrgManage.getUserNo());
		uapUserOrg.setOrgNo(uapOrg.getNo());
		userOrgList.add(uapUserOrg);
	}

	private void addUserOrgManage(UapUser user, UapUserOrgManage uapUserOrgManage, List<UapUserOrgManage> userOrgList) {
		UapUserOrgManage uapUserOrg = new UapUserOrgManage();
		uapUserOrg.setIsCascade(uapUserOrgManage.getIsCascade());
		uapUserOrg.setOrgId(uapUserOrgManage.getOrgId());
		uapUserOrg.setOrgName(uapUserOrgManage.getOrgName());
		uapUserOrg.setUpTime(DateTimeUtil.clock.millis());
		uapUserOrg.setUserId(user.getId());
		uapUserOrg.setUserName(user.getName());
		uapUserOrg.setUserNo(user.getNo());
		uapUserOrg.setOrgNo(uapUserOrgManage.getOrgNo());
		userOrgList.add(uapUserOrg);

	}

	private List<UapOrganization> findAllSubOrg(UapOrganization org, Long tenancyId) {
		List<UapOrganization> result = Lists.newArrayList();
		OrgQuery orgQuery = new OrgQuery();
		int pageCount = 2000;
		orgQuery.setLimitMax(false);
		orgQuery.setStart(0);
		orgQuery.setLimit(pageCount);
		orgQuery.setPathId(org.getOrgPathId());
		orgQuery.setTenancyId(tenancyId);
		PageData<UapOrganization> pageData = orgCustomRepository.findSubOrgsByPathId(orgQuery);
		if (CollectionUtils.isEmpty(pageData.getData())) {
			return result;
		}
		result.addAll(pageData.getData());
		long total = pageData.getTotal();
		System.out.println("total is " + total);
		if (total > pageCount) {
			long loops = total % pageCount == 0 ? total / pageCount : total / pageCount + 1;
			for (int i = 1; i < loops; i++) {
				orgQuery.setStart(pageCount * i);
				pageData = orgCustomRepository.findSubOrgsByPathId(orgQuery);
				result.addAll(pageData.getData());
			}
		}
		return result;
	}

//	public void testCreateOrgData(Long id) {
//		int count = 10000;
//		List<Object[]> batchArgs = new ArrayList<Object[]>();
//		// Long id = 109999L;
//		for (int i = 0; i < count; i++) {
//			id += 1;
//			batchArgs.add(new Object[] { id, null, null, null, null, i, id, 100000L, i, "1", null, null, null, null,
//					"100000->" + id, 1 });
//		}
//		userOrgManageCustomRepository.batchInsertOrgTest(batchArgs);
//	}
	// public List<UapUser> findUserByOrg(Long orgId) {
	// UapOrganization uapOrganization = organizationService.get(orgId);
	// Long tenancyId = uapOrganization.getUapMultiTenancy().getId();
	//// if (null == uapOrganization) {
	//// return Collections.emptyList();
	//// }
	//// uapOrganization = organizationService.findRootOrg(uapOrganization);
	//// if (null == uapOrganization ||
	// StringUtils.isEmpty(uapOrganization.getNo())) {
	//// return Collections.emptyList();
	//// }
	// List<UapUser> result = Lists.newArrayList();
	// UserQuery userQuery = new UserQuery();
	// int pageCount = 30;
	// userQuery.setStart(0);
	// userQuery.setLimit(pageCount);
	// userQuery.setOrgNo(uapOrganization.getNo());
	// userQuery.setTenancyId(tenancyId);
	// PageData<UapUser> pageData =
	// userCustomRepository.findUsersByOrgNo(userQuery);
	// if (CollectionUtils.isEmpty(pageData.getData())) {
	// return result;
	// }
	// result.addAll(pageData.getData());
	// long total = pageData.getTotal();
	// if (total > pageCount) {
	// long loops = total % pageCount == 0 ? total / pageCount : total /
	// pageCount + 1;
	// for (int i = 1; i < loops; i++) {
	// userQuery.setStart(pageCount * i);
	// pageData = userCustomRepository.findUsersByOrgNo(userQuery);
	// result.addAll(pageData.getData());
	// }
	// }
	// return result;
	// }

}
