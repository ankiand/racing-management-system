'use strict';
import * as ng1 from "angular";
import * as _ from "lodash";
export const appModule: ng1.IModule = ng1.module('app.module');
const htmlTemplate = require("./registerModal.tpl.html");


appModule.service('registerModal', ['$uibModal', registerModalServiceFactory]);

function registerModalServiceFactory($uibModal) {
  return {
    show: _.partial(showRegisterModal, $uibModal)
  };
}

function showRegisterModal($uibModal) {
  let modalInstance = $uibModal.open({
    template: htmlTemplate,
    controllerAs: 'vm',
    controller: ['$uibModalInstance', Ctrl],
    size: 'sm'
  });

  return modalInstance.result;
}

function Ctrl ($uibModalInstance) {
  let vm = this;
  vm.submit = submit;
  vm.cancel = cancel;
  vm.checkPassword = checkPassword;
  vm.password2 = undefined;
  vm.user = {
    userId: undefined,
    displayName: undefined,
    organisation: undefined,
    password: undefined
  };

  function checkPassword(userInfoForm) {
    if (userInfoForm.password.$dirty) {
      userInfoForm.password.$setValidity('passwordTooSmall', vm.user.password !== undefined && vm.user.password.length >= 4);
    }
    if (userInfoForm.password.$dirty && userInfoForm.password2.$dirty) {
      userInfoForm.password.$setValidity('passwordMismatch', vm.password2 === vm.user.password);
      userInfoForm.password2.$setValidity('passwordMismatch', vm.password2 === vm.user.password);
      return userInfoForm.password.$valid && userInfoForm.password2.$valid;
    }
    return true;
  }

  function submit(userInfoForm) {
    vm.submitted = true;
    checkPassword(userInfoForm);
    if (userInfoForm.$valid) {
      $uibModalInstance.close(vm.user);
    } else {

    }
  }

  function cancel() {
    $uibModalInstance.dismiss('cancel');
  }
}
