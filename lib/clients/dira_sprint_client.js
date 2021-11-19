'use strict';
const { DiraClient } = require("./dira_client");

class DiraSprintClient extends DiraClient {
    constructor(project_id) {
        super();
        this.base_url = `${this.base_url}/projects/${project_id}/sprints`;
    }

    get_all_sprints() {
        return this.get({
<<<<<<< HEAD
            url: `${thi.base_url}`
=======
            url: `${this.base_url}`
>>>>>>> dev
        });
    }

    create_sprint(sprintModel) {
        return this.post({
            url: `${this.base_url}`,
            data: sprintModel
        });
    }

    get_sprint_with_id(sprint_id) {
        return this.get({
            url: `${this.base_url}/${sprint_id}`
        });
    }

    update_sprint(sprint_id, sprint_model) {
        return this.put({
            url: `${this.base_url}/${sprint_id}`,
            data: sprint_model
        });
    }

    delete_sprint(sprint_id) {
        return this.delete({
<<<<<<< HEAD
            url: `${this.base_url}`
=======
            url: `${this.base_url}/${sprint_id}`
>>>>>>> dev
        });
    }
}

module.exports = DiraSprintClient;
