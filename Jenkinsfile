stage('Deploy to EKS using Ansible') {
    steps {
        sh '''
            set -e

            echo "===== ANSIBLE EKS DEPLOYMENT ====="

            ansible-playbook \
              ansible/deploy-bookmyshow.yml \
              -i ansible/inventory.ini \
              -e "image=${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
        '''
    }
}
