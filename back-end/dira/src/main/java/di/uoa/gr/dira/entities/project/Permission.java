package di.uoa.gr.dira.entities.project;

import di.uoa.gr.dira.entities.customer.Customer;
import di.uoa.gr.dira.shared.PermissionType;
import di.uoa.gr.dira.shared.PermissionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer user;

    private int permission;

    public void setPermissionFromPermissionSet(Set<PermissionTypeEnum> permissions) {
        permission = PermissionType.fromPermissionSet(permissions).getPermission();
    }
}
