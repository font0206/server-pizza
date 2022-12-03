package app.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class Role {
	private Long id;

	private RoleName name;

	public Role(RoleName name) {
		this.name = name;
	}
}
