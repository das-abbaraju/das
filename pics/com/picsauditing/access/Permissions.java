package com.picsauditing.access;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Permissions{
	
	private int userid;
	private boolean loggedIn = false;
	private List<Integer> groups = new ArrayList();
	private Set<Permission> permissions = new HashSet<Permission>();	
	private String username;
	private int accountID;
	
	public Permissions(User user){
		userid = Integer.parseInt(user.userDO.id);
		loggedIn = true;
		username = user.userDO.username;
		accountID = Integer.parseInt(user.userDO.accountID);		
		try{
			permissions = user.getPermissions();
			List<User> temp = user.getGroups();
			for(User u : temp)
				groups.add(Integer.parseInt(u.userDO.id));
		}catch(Exception ex){
			
		}		
		
	}	
	
	public int getUserid() {
		return userid;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public List<Integer> getGroups() {
		return groups;
	}
	public String getUsername() {
		return username;
	}
	public int getAccountID() {
		return accountID;
	}
	
	public boolean hasPermission(OpPerms opPerm, OpType oType){		
		
		boolean typeFlag = false;
		for(Permission perm : permissions){
			OpPerms permType = perm.getAccessType();			
			if(oType == OpType.Grant)
				 typeFlag =perm.isGrantFlag();
			else if(oType == OpType.Edit)
				typeFlag = perm.isEditFlag();			
			else if(oType ==OpType.Delete)
				typeFlag = perm.isDeleteFlag();
			else 
				typeFlag = perm.isViewFlag();		
			
			if(permType ==  opPerm && typeFlag)
				return true;
		}
		
		return false;
	}
	
	public boolean hasGroup(Integer group){
		for(Integer i : groups)
			if(i == group)
				return true;
		
		return false;
	}
	
	
}
