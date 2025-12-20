module.exports = async ({github, context: {owner, repo}}) => {
  const title = process.env.MILESTONE_TITLE;
  const query = `
  query ($owner: String!, $repo: String!, $title: String!) {
    repository(owner: $owner, name: $repo) {
      milestones(first: 100, query: $title) {
        nodes {
          title
          number
          openIssueCount
        }
      }
    }
  }
  `;
  const {repository} = await github.graphql(query, {owner, repo, title});
  const [milestone] = repository.milestones.nodes.filter(it => it.title === title);
  if (!milestone) {
    throw new Error(`Milestone "${title}" not found`);
  }
  if (milestone.openIssueCount > 0) {
    throw new Error(`Milestone "${title}" has ${milestone.openIssueCount} open issue(s)`);
  }
  const requestBody = {
    owner,
    repo,
    milestone_number: milestone.number,
    state: 'closed',
    due_on: new Date().toISOString()
  };
  console.log(requestBody);
  await github.rest.issues.updateMilestone(requestBody);
};
