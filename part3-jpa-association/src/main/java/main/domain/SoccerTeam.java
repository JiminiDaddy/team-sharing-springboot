package main.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SoccerTeam {
	@Id @GeneratedValue
	private Long id;

	private String name;

	@OneToMany(mappedBy = "team")
	private List<SoccerMember> members = new ArrayList<>();

	protected SoccerTeam() {}

	public SoccerTeam(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<SoccerMember> getMembers() {
		return members;
	}

	public void addMember(SoccerMember member) {
		members.add(member)	;
	}
}
