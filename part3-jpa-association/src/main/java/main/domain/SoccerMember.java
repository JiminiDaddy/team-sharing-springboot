package main.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SoccerMember {
	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToOne(optional = false)
	private SoccerTeam team;

	protected SoccerMember() {}

	public SoccerMember(String name, SoccerTeam team) {
		this.name = name;
		this.team = team;
		this.team.addMember(this);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public SoccerTeam getTeam() {
		return team;
	}

	public void changeTeam(SoccerTeam team) {
		if (this.team != null) {
			this.team.getMembers().remove(this);
		}
		this.team = team;
		this.team.addMember(this);
	}
}
