package di.uoa.gr.dira.repositories;

import di.uoa.gr.dira.entities.issue.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {
}
